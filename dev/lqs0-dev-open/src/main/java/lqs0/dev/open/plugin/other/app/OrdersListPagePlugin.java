package lqs0.dev.open.plugin.other.app;

import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.MobileFormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.plugin.AbstractMobListPlugin;

import java.util.EventObject;

public class OrdersListPagePlugin extends AbstractMobListPlugin {
    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs){
        super.beforeDoOperation(eventArgs);

        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();

        if (StringUtils.equals("orderdetails",opKey)){
            orderDetails(eventArgs);
        } else if (StringUtils.equals("goreview",opKey)){
            goreView(eventArgs);
        }
    }

    private void goreView(BeforeDoOperationEventArgs eventArgs) {
        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段
        //获取当前点击的菜品的主键值
        Object orderId = listColumns.get(0).getPrimaryKeyValue();

        MobileFormShowParameter showParameter = new MobileFormShowParameter();
        showParameter.setFormId("lqs0_dish_evaluate_mob");
        showParameter.getOpenStyle().setShowType(ShowType.Floating);
        showParameter.setCustomParam("orderId",orderId);
        // 显示表单
        this.getView().showForm(showParameter);

    }

    private void orderDetails(BeforeDoOperationEventArgs eventArgs) {
        //若想打开该页面，必须传入订单的id
        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段
        //获取当前点击的菜品的主键值
        Object orderId = listColumns.get(0).getPrimaryKeyValue();

        MobileFormShowParameter showParameter = new MobileFormShowParameter();

        showParameter.setFormId("lqs0_order_detail_page");

        showParameter.getOpenStyle().setShowType(ShowType.Floating);

        showParameter.setCustomParam("orderId",orderId);

        // 显示表单
        this.getView().showForm(showParameter);
    }


}
