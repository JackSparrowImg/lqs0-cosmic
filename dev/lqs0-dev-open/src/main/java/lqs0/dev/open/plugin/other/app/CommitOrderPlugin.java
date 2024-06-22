package lqs0.dev.open.plugin.other.app;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;

import kd.bos.entity.operate.result.OperationResult;
import kd.bos.form.MobileFormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Label;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.BillList;

import kd.bos.list.plugin.AbstractMobListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;


import java.math.BigDecimal;
import java.util.*;

public class CommitOrderPlugin extends AbstractMobListPlugin {

    @Override
    public void beforeBindData(EventObject e) {
        super.beforeBindData(e);
        Object shopId = this.getView().getFormShowParameter().getCustomParam("shopId");
        QFilter qFilter = new QFilter("lqs0_dishname.lqs0_shopname.id",QCP.equals,shopId);

        BillList billList = this.getControl("lqs0_billlistap");
        billList.setFilter(qFilter);


        //根据主键值过滤，找到商店的名称信息
        QFilter idFilter = new QFilter("id", QCP.equals,shopId);
        DynamicObject targerShop = BusinessDataServiceHelper.loadSingle("lqs0_shop",
                "name" , new QFilter[]{idFilter});

        Label label = this.getView().getControl("lqs0_shopname");
        // 设置标签控件的值
        if (label != null) {
            Map<String,String> shopName = (Map<String, String>) targerShop.get("name");
            label.setText(shopName.get("GLang"));
        }

        //设置配送费和打包费
        Label dabao = this.getView().getControl("lqs0_dabaofei1");
        dabao.setText("￥ 1");
        Label peisong = this.getView().getControl("lqs0_peisongfei1");
        peisong.setText("￥ 1");
    }
    @Override
    public void afterBindData(EventObject eventObject){
        super.afterBindData(eventObject);
        BillList billList = this.getControl("lqs0_billlistap");

        //根据菜品中的店铺中的id属性进行过滤
        Object shopId = this.getView().getFormShowParameter().getCustomParam("shopId");
        QFilter qFilter = new QFilter("lqs0_dishname.lqs0_shopname.id",QCP.equals,shopId);

        Object userId = RequestContext.get().getCurrUserId();
        QFilter userFilter = new QFilter("creator.id",QCP.equals,userId);

        DynamicObject[] myShoppingCar = BusinessDataServiceHelper.load("lqs0_shoppingcar",
                "number,lqs0_dishname,lqs0_shopname,lqs0_count," +
                        "lqs0_amount,lqs0_image,creator",new QFilter[]{qFilter,userFilter});

        double allMoney = 0;

        for (DynamicObject shop : myShoppingCar){
            int count = (int) shop.get("lqs0_count");
            BigDecimal amount = (BigDecimal) shop.get("lqs0_dishname.lqs0_price");

            double price = amount.doubleValue();

            //TODO: 计算总金额
            allMoney += count * price + 2;
        }

        String  str = String.format("%.2f",allMoney);
        allMoney = Double.parseDouble(str);
        this.getModel().setValue("lqs0_amount",allMoney);
        Label label = this.getView().getControl("lqs0_allmoney1");
        // 设置标签控件的值
        if (label != null) {
            label.setText("￥ " + allMoney);
        }
    }

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs){
        super.beforeDoOperation(eventArgs);

        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();
        if (StringUtils.equals("topay",opKey)){
            toPay(eventArgs);
        }
    }

    private void toPay(BeforeDoOperationEventArgs eventArgs) {
        MobileFormShowParameter showParameter = new MobileFormShowParameter();

        showParameter.setFormId("lqs0_pay");

        // 设置显示样式为模态窗口
        showParameter.getOpenStyle().setShowType(ShowType.Floating);

        Object shopId = this.getView().getFormShowParameter().getCustomParam("shopId");

        Object value = this.getModel().getValue("lqs0_amount");

        Object lqs0Address = this.getView().getModel().getValue("lqs0_address");
        showParameter.setCustomParam("shopId",shopId);
        showParameter.setCustomParam("addressId",lqs0Address);
        showParameter.setCustomParam("allmoney",value);
        Long selfOrderId = createReadyOrder(eventArgs,shopId,lqs0Address,value);
        showParameter.setCustomParam("selfOrderId",selfOrderId);

        //订单创建完成删除自己的购物车
        //clearnShoppingCar(shopId);


        // 显示表单
        this.getView().showForm(showParameter);
    }

    private Long createReadyOrder(BeforeDoOperationEventArgs eventArgs, Object shopId, Object address, Object allmoney) {

        DynamicObject lqs0Address = (DynamicObject) address;

        // 1.首先将购物车当前用户的菜品信息查询处理啊
        QFilter qFilter = new QFilter("lqs0_dishname.lqs0_shopname.id",QCP.equals,shopId);
        Object userId = RequestContext.get().getCurrUserId();

        QFilter userFilter = new QFilter("creator.id",QCP.equals,userId);
        DynamicObject[] myShoppingCar = BusinessDataServiceHelper.load("lqs0_shoppingcar",
                "number,lqs0_dishname,lqs0_shopname,lqs0_count," +
                        "lqs0_amount,lqs0_image,creator,lqs0_flavor",new QFilter[]{qFilter,userFilter});

        QFilter addressFilter = new QFilter("id",QCP.equals,lqs0Address.get("id"));
        DynamicObject myAddressBook = BusinessDataServiceHelper.loadSingle("lqs0_address_book",
                "number,lqs0_userfield,lqs0_label,lqs0_is_default," +
                        "lqs0_phone,lqs0_address",new QFilter[]{addressFilter});

        QFilter shopFilter =  new QFilter("id",QCP.equals,shopId);
        DynamicObject shop = BusinessDataServiceHelper.loadSingle("lqs0_shop","name",new QFilter[]{shopFilter});

        //创建订单数据
        DynamicObject selfOrder = BusinessDataServiceHelper.newDynamicObject("lqs0_order");
        String uniqueID = "jiji_order_" + UUID.randomUUID().toString().substring(0,5);
        selfOrder.set("number",uniqueID);
        selfOrder.set("status","C");
        selfOrder.set("enable","1");
        selfOrder.set("creator",RequestContext.get().getCurrUserId());
        selfOrder.set("lqs0_user",RequestContext.get().getCurrUserId());
        selfOrder.set("lqs0_status",1); // 设置订单状态，1：待付款，2：待接单，3：已结单，4：派送中，5：已完成，6：已取消
        selfOrder.set("lqs0_order_time",new Date());
        selfOrder.set("lqs0_pay_status",2); // 设置支付状态，1：已支付，2：未支付，3：退款
        selfOrder.set("lqs0_amount",allmoney);
        selfOrder.set("lqs0_address_num",lqs0Address);
        selfOrder.set("lqs0_xuan",1);
        selfOrder.set("lqs0_address_num",myAddressBook);
        selfOrder.set("lqs0_shopname",shop.get("name")); //给店铺名赋值
        selfOrder.set("lqs0_pack_amount","1"); //打包费为一元
        selfOrder.set("lqs0_tableware_number","商家按量提供");
        //TODO:预计送达时间

        for (DynamicObject shoppingCar : myShoppingCar) {
            selfOrder.set("lqs0_display_dish", shoppingCar.get("lqs0_dishname.lqs0_dishimg"));
        }

        //执行新增语句
        OperationResult saveResult = SaveServiceHelper.saveOperate("lqs0_order",new DynamicObject[]{selfOrder}, OperateOption.create());
        List<Object> successPkIds = saveResult.getSuccessPkIds();
        System.out.println(successPkIds.get(0));

        //向订单详情表中添加菜品信息
        for (DynamicObject shoppingCar : myShoppingCar){
            DynamicObject orderDetail = BusinessDataServiceHelper.newDynamicObject("lqs0_order_detail");
            String detailUniqueID = "jiji_detail_" + UUID.randomUUID().toString().substring(0,5);
            orderDetail.set("number",detailUniqueID);


            orderDetail.set("lqs0_order_number",selfOrder);
            //TODO:获取菜品名称
            Map<String,String> dishname = (Map<String, String>) shoppingCar.get("lqs0_dishname.name");

            orderDetail.set("lqs0_dish_name",dishname.getOrDefault("zh_CN", "0"));
            orderDetail.set("lqs0_num",shoppingCar.get("lqs0_count"));
            orderDetail.set("lqs0_dish_image",shoppingCar.get("lqs0_dishname.lqs0_dishimg"));
            orderDetail.set("lqs0_dish_flavor",shoppingCar.get("lqs0_dishname.lqs0_flavor"));
            orderDetail.set("lqs0_amount",shoppingCar.get("lqs0_dishname.lqs0_price"));
            orderDetail.set("lqs0_dish_number",shoppingCar.get("lqs0_dishname.id"));

            orderDetail.set("status","C");
            orderDetail.set("enable","1");
            orderDetail.set("creator",RequestContext.get().getCurrUserId());
            SaveServiceHelper.saveOperate("lqs0_order_detail",new DynamicObject[]{orderDetail}, OperateOption.create());
        }
        System.out.println("新增订单成功!!!");
        return (Long) selfOrder.getPkValue();
    }

    private void clearnShoppingCar(Object shopId) {

        QFilter qFilter = new QFilter("lqs0_dishname.lqs0_shopname.id",QCP.equals,shopId);
        Object userId = RequestContext.get().getCurrUserId();
        QFilter userFilter = new QFilter("creator.id",QCP.equals,userId);
        DeleteServiceHelper.delete("lqs0_shoppingcar",new QFilter[]{qFilter,userFilter});
    }
}
