package lqs0.dev.open.plugin.other.app;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;

import kd.bos.list.plugin.AbstractMobListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

import java.util.List;

public class ShoppingCarPlugin extends AbstractMobListPlugin {

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs){
        super.beforeDoOperation(eventArgs);

        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段
        System.out.println(listColumns);

        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();

        if (StringUtils.equals("reducecount",opKey)){
            ReduceCount(eventArgs);
        }else if (StringUtils.equals("addcount",opKey)){
            AddCount(eventArgs);
        }

    }

    @Override
        public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
            super.afterDoOperation(afterDoOperationEventArgs);
            this.getView().updateView();
        }

    private void AddCount(BeforeDoOperationEventArgs eventArgs) {
        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段

        //获取当前点击的菜品的主键值
        Object primaryKey = listColumns.get(0).getPrimaryKeyValue();
        //根据过滤器查询购物车中是否存在该商品
        QFilter idFilter = new QFilter("id",QCP.equals,primaryKey);
        DynamicObject myShoppingCar = BusinessDataServiceHelper.loadSingle("lqs0_shoppingcar",
                "number,lqs0_dishname,lqs0_shopname,lqs0_count," +
                        "lqs0_amount,lqs0_image,creator",new QFilter[]{idFilter});

        if (null != myShoppingCar){
            int count = (int) myShoppingCar.get("lqs0_count");

            //存在该商品，则将该商品的数量加一，并保存，无上限限制
            myShoppingCar.set("lqs0_count",count + 1);
            OperationResult saveResult = SaveServiceHelper.saveOperate("lqs0_shoppingcar",
                    new DynamicObject[]{myShoppingCar}, OperateOption.create());
            List<Object> successPkIds = saveResult.getSuccessPkIds();
            System.out.println(successPkIds.get(0));
            //this.getView().showMessage("数量加一！");
        }
    }

    private void ReduceCount(BeforeDoOperationEventArgs eventArgs) {
        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段
        //获取当前点击的菜品的主键值
        Object primaryKey = listColumns.get(0).getPrimaryKeyValue();

        QFilter idFilter = new QFilter("id",QCP.equals,primaryKey);
        DynamicObject myShoppingCar = BusinessDataServiceHelper.loadSingle("lqs0_shoppingcar",
                "number,lqs0_dishname,lqs0_shopname,lqs0_count," +
                        "lqs0_amount,lqs0_image,creator",new QFilter[]{idFilter});
        if(null != myShoppingCar){
            int count = (int) myShoppingCar.get("lqs0_count");
            //数量为一，直接将该购物车中的菜品信息删除
            if (count <= 1){
                QFilter PKFilter = new QFilter("id", QCP.equals, myShoppingCar.getPkValue());
                DeleteServiceHelper.delete("lqs0_shoppingcar", new QFilter[]{PKFilter});
               // this.getView().showMessage("该商品已被删除！");
                this.refreshData();
                this.getView().updateView();
            }else {
                //否则将数量减一，并保存
                myShoppingCar.set("lqs0_count",count - 1);
                OperationResult saveResult = SaveServiceHelper.saveOperate("lqs0_shoppingcar",
                        new DynamicObject[]{myShoppingCar}, OperateOption.create());
                List<Object> successPkIds = saveResult.getSuccessPkIds();
                System.out.println(successPkIds.get(0));
                //this.getView().showMessage("数量减一！");
            }
        }
    }

}
