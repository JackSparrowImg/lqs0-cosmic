package lqs0.dev.open.plugin.other.app;

import com.alibaba.fastjson.JSONObject;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.serialization.DataEntitySerializer;
import kd.bos.dataentity.serialization.DataEntitySerializerOption;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.dtx.util.DynamicObjectSerializeUtil;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.CloseCallBack;
import kd.bos.form.IPageCache;
import kd.bos.form.MobileFormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.MobileListShowParameter;
import kd.bos.list.plugin.AbstractMobListPlugin;
import kd.bos.metadata.print.control.DynamicUnit;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import lqs0.dev.open.utils.ShoppingCarUtils;
import scala.Dynamic;

import java.util.HashMap;
import java.util.Map;


// 菜品管理移动端插件


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
                        "lqs0_stockquantity,lqs0_shopname,lqs0_dishimg,creator,lqs0_dish_size_info,lqs0_dish_flavor_info" +
                        ",lqs0_dish_size,lqs0_size_price,lqs0_flavor_info_all",
                new QFilter[]{idFilter});

        //首先判断一个当前的菜品是否存在口味，规格信息
        DynamicObjectCollection dishSizeInfo = chooseDish.getDynamicObjectCollection("lqs0_dish_size_info");
        DynamicObjectCollection dishFlavorInfo = chooseDish.getDynamicObjectCollection("lqs0_dish_flavor_info");
        //如果存在口味等信息，先打开菜品口味选择界面
        if(dishFlavorInfo != null && dishFlavorInfo.size() > 0 || dishSizeInfo != null && dishSizeInfo.size() > 0){
            MobileFormShowParameter showParameter = new MobileFormShowParameter();
            showParameter.setFormId("lqs0_dish_flavor_select");
            showParameter.setCustomParam("dishNumber",chooseDish.get("number"));
            showParameter.setCustomParam("dishName",chooseDish.get("name").toString());
            showParameter.getOpenStyle().setShowType(ShowType.Modal);

            Map<String, String> m = new HashMap<>(1);
            m.put("position", "center");
            showParameter.getOpenStyle().setCustParam(m);

            this.getView().showForm(showParameter);
        }else if(null != chooseDish){
            /**
             * 构造菜品主键信息进行过滤
             */
            Object dishPrimaryKey =  chooseDish.getPkValue();
            QFilter dishFilter = new QFilter("lqs0_dishname.masterid",QCP.equals,dishPrimaryKey);
            DynamicObject myShoppingCar = BusinessDataServiceHelper.loadSingle("lqs0_shoppingcar",
                    "number,lqs0_dishname,lqs0_shopname,lqs0_count," +
                            "lqs0_amount,lqs0_image,creator",new QFilter[]{dishFilter});

            ShoppingCarUtils shoppingCarPlugin = new ShoppingCarUtils();
            boolean result = shoppingCarPlugin.addDishToShoppingCar(chooseDish);
            if(result == true){
                this.getView().showMessage("菜品成功加入购物车！");
            }else{
                this.getView().showMessage("商品太火爆了，稍等重试！");
            }
        }
        System.out.println(chooseDish);
    }


    private void ShowShoppingCar(BeforeDoOperationEventArgs eventArgs) {
        MobileFormShowParameter showParameter = new MobileFormShowParameter();

        showParameter.setFormId("lqs0_shoppingcar_mob");
        // 设置显示样式为模态窗口
        showParameter.getOpenStyle().setShowType(ShowType.Modal);

        Map<String, String> m = new HashMap<>(1);
        m.put("position", "bottom");
        showParameter.getOpenStyle().setCustParam(m);

        Object shopId = this.getView().getFormShowParameter().getCustomParam("shopId");
        //根据菜品中的店铺中的id属性进行过滤
        showParameter.setCustomParam("shopId",shopId);


        // 显示表单
        this.getView().showForm(showParameter);
    }

}
