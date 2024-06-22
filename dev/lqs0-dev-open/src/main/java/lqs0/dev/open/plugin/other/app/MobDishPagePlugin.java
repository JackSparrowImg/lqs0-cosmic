package lqs0.dev.open.plugin.other.app;

import com.kingdee.cosmic.ctrl.common.CtrlUtil;
import kd.bos.bill.MobileBillShowParameter;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.entity.filter.FilterParameter;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.form.MobileFormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.BeforeBindDataEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.BillList;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.MobileListShowParameter;
import kd.bos.list.plugin.AbstractMobListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

import java.util.List;
import java.util.UUID;


public class MobDishPagePlugin extends AbstractMobListPlugin{


    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs){
        super.beforeDoOperation(eventArgs);

        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段
        System.out.println(listColumns);

        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();

        if (StringUtils.equals("showshoppingcar",opKey)){
            ShowShoppingCar(eventArgs);
        }else if (StringUtils.equals("addtoshoppingcar",opKey)){
            AddToShoppingCar(eventArgs);
        }else if (StringUtils.equals("clearing",opKey)){
            CheckOutFoodList(eventArgs);
        }

    }

    private void CheckOutFoodList(BeforeDoOperationEventArgs eventArgs) {

        MobileFormShowParameter showParameter = new MobileFormShowParameter();

        //打开菜品提交页面
        showParameter.setFormId("lqs0_commit_order");
        // 设置显示样式为模浮动
        showParameter.getOpenStyle().setShowType(ShowType.Floating);

        //获取当前进入商铺的id
        Object shopId = this.getView().getFormShowParameter().getCustomParam("shopId");

        //将店铺id传递到订单提交页面，方便该过滤数据
        showParameter.setCustomParam("shopId",shopId);


        // 显示表单
        this.getView().showForm(showParameter);
    }

    private void AddToShoppingCar(BeforeDoOperationEventArgs eventArgs) {
        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段

        //获取当前点击的菜品的主键值
        Object primaryKey = listColumns.get(0).getPrimaryKeyValue();

        //根据主键值过滤，找到该菜品的信息
        QFilter idFilter = new QFilter("id", QCP.equals,primaryKey);
        DynamicObject chooseDish = BusinessDataServiceHelper.loadSingle("lqs0_dish",
                "name,lqs0_caixi,lqs0_category,lqs0_price," +
                        "lqs0_stockquantity,lqs0_shopname,lqs0_dishimg,creator",
                new QFilter[]{idFilter});
        //选择的菜品不为空
        if(null != chooseDish){
            //判断购物车中是否有该菜品
            //首先将购物车中的数据查询出来

            /**
             * 构造菜品主键信息进行过滤
             */
            Object dishPrimaryKey =  chooseDish.getPkValue();
            QFilter dishFilter = new QFilter("lqs0_dishname.masterid",QCP.equals,dishPrimaryKey);
            DynamicObject myShoppingCar = BusinessDataServiceHelper.loadSingle("lqs0_shoppingcar",
                    "number,lqs0_dishname,lqs0_shopname,lqs0_count," +
                            "lqs0_amount,lqs0_image,creator",new QFilter[]{dishFilter});
            //有，则将数量加一
            if (null != myShoppingCar){
                int count = (int) myShoppingCar.get("lqs0_count") + 1;
                myShoppingCar.set("lqs0_count",count);
                SaveServiceHelper.saveOperate("lqs0_shoppingcar", new DynamicObject[]{myShoppingCar}, OperateOption.create());
                //this.getView().showMessage("购物车中已存在该菜品，数量加一！");
            } else {
                //否则，想购物车中添加一条菜品记录
                DynamicObject selfShoppingCar = BusinessDataServiceHelper.newDynamicObject("lqs0_shoppingcar");
                selfShoppingCar.set("status","C");
                selfShoppingCar.set("enable","1");
                Integer count = 1;
                selfShoppingCar.set("lqs0_count",count);
                String uniqueID = "jiji_shoppingcar_" + UUID.randomUUID().toString().substring(0,3);
                selfShoppingCar.set("number",uniqueID);
                selfShoppingCar.set("creator", RequestContext.get().getCurrUserId());


                /*DynamicObject lqs0Shopname = (DynamicObject) chooseDish.get("lqs0_shopname");
                Object shopPrimaryKey =  lqs0Shopname.getPkValue();
                QFilter shopFilter = new QFilter("id",QCP.equals,shopPrimaryKey);
                DynamicObject targetShopName = BusinessDataServiceHelper
                        .loadSingle("lqs0_shop","number,name",new QFilter[]{shopFilter});
                selfShoppingCar.set("lqs0_shopname",targetShopName);*/

                selfShoppingCar.set("lqs0_dishname",chooseDish);

                OperationResult saveResult = SaveServiceHelper.saveOperate("lqs0_shoppingcar",
                        new DynamicObject[]{selfShoppingCar}, OperateOption.create());
                List<Object> successPkIds = saveResult.getSuccessPkIds();
                System.out.println(successPkIds.get(0));

                this.getView().showMessage("已经加入购物车！");
            }

        }

        System.out.println(chooseDish);
    }

    private void ShowShoppingCar(BeforeDoOperationEventArgs eventArgs) {
        MobileListShowParameter showParameter = new MobileListShowParameter();

        showParameter.setBillFormId("lqs0_shoppingcar"); // 这里是列表对应的单据标识
        showParameter.setFormId("bos_moblist"); // 这里是列表的模板标识，可以在设计器列表页寻找

        // 设置显示样式为模态窗口
        showParameter.getOpenStyle().setShowType(ShowType.Modal);
        showParameter.setHeight("40%");

        Object shopId = this.getView().getFormShowParameter().getCustomParam("shopId");


        //根据菜品中的店铺中的id属性进行过滤
        QFilter qFilter = new QFilter("lqs0_dishname.lqs0_shopname.id",QCP.equals,shopId);
        ListFilterParameter filterParameter = new ListFilterParameter();
        filterParameter.setFilter(qFilter);
        showParameter.setListFilterParameter(filterParameter);

        // 显示表单
        this.getView().showForm(showParameter);
    }

}
