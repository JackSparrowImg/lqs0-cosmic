package lqs0.dev.open.plugin.other;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.ShowType;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.operatecol.OperationColItem;
import kd.bos.list.BillList;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.ListShowParameter;
import kd.bos.list.MobileListShowParameter;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;


public class OrderListPlugin extends AbstractBillPlugIn {

    private static String LIST_KEY = "lqs0_billlistap";

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs){
        super.beforeDoOperation(eventArgs);
        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();

        switch (opKey){
            case "openorderdetails": openOrderDetails(eventArgs); break;
            case "receiving_order": receivingOrder(eventArgs); break;
            case "refusal_order": refusalOrder(eventArgs); break;
            case "delivery_order": deliveryOrder(eventArgs); break;
            case "complete_order": completeOrder(eventArgs); break;
            default:break;
        }
        BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
        billList.refresh();
    }

    private void completeOrder(BeforeDoOperationEventArgs eventArgs) {

        DynamicObject targetOrder = getTargetDynamicObject();

        if (refusalOrNo(targetOrder)) return;

        Long lqs0Status = Long.parseLong(targetOrder.get("lqs0_status").toString());
        if (lqs0Status <= 3){
            this.getView().showMessage("为用户配送后，然后才能完成该订单奥");
            return;
        }
        targetOrder.set("lqs0_status",5);  //订单完成
        SaveServiceHelper.saveOperate("lqs0_order", new DynamicObject[]{targetOrder}, OperateOption.create());
        this.getView().showMessage("订单已完成，幸苦您了");
    }

    private void deliveryOrder(BeforeDoOperationEventArgs eventArgs) {
        DynamicObject targetOrder = getTargetDynamicObject();

        if (refusalOrNo(targetOrder)) return;

        Long lqs0Status = Long.parseLong(targetOrder.get("lqs0_status").toString());
        if (lqs0Status <= 2){
            this.getView().showMessage("请先完成接单，然后才能为用户配送奥");
            return;
        }
        targetOrder.set("lqs0_status",4);  // 订单进入配送
        SaveServiceHelper.saveOperate("lqs0_order", new DynamicObject[]{targetOrder}, OperateOption.create());
        this.getView().showMessage("订单配送中，请及时送达客户");
    }

    private void refusalOrder(BeforeDoOperationEventArgs eventArgs) {
        DynamicObject targetOrder = getTargetDynamicObject();

        Long lqs0Status = Long.parseLong(targetOrder.get("lqs0_status").toString());

        if (lqs0Status != 8 && lqs0Status != 2){
            this.getView().showMessage("已接单，无法拒单");
            return;
        }

        targetOrder.set("lqs0_status",8); // 拒绝接单
        SaveServiceHelper.saveOperate("lqs0_order", new DynamicObject[]{targetOrder}, OperateOption.create());
        this.getView().showMessage("拒单成功,已为用户退款");
    }

    private void receivingOrder(BeforeDoOperationEventArgs eventArgs) {
        DynamicObject targetOrder = getTargetDynamicObject();

        if (refusalOrNo(targetOrder)) return;

        targetOrder.set("lqs0_status",3);  // 接单
        SaveServiceHelper.saveOperate("lqs0_order", new DynamicObject[]{targetOrder}, OperateOption.create());
        this.getView().showMessage("接单成功，请及时出餐奥");
    }

    private void openOrderDetails(BeforeDoOperationEventArgs eventArgs) {
        ListShowParameter showParameter = new ListShowParameter();
        showParameter.setBillFormId("lqs0_order_detail"); // 这里是列表对应的单据标识
        showParameter.setFormId("bos_list"); // 这里是列表的模板标识，可以在设计器列表页寻找
        //TODO:打开新的标签页，如果不合适继续修改
        showParameter.getOpenStyle().setShowType(ShowType.Modal);

        //TODO:获取基础资料列表选中数据模板
        BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
        ListSelectedRowCollection selectedRows = billList.getSelectedRows();

        Object[] primaryKeyValues = selectedRows.getPrimaryKeyValues();
        QFilter idFilter = new QFilter("lqs0_order_number", QCP.equals,primaryKeyValues[0]);

        ListFilterParameter filterParameter = new ListFilterParameter();
        filterParameter.setFilter(idFilter);
        showParameter.setListFilterParameter(filterParameter);


        this.getView().showForm(showParameter);
    }


    public DynamicObject getTargetDynamicObject(){
        BillList billList = this.getControl(AbstractListPlugin.BILLLISTID);
        ListSelectedRowCollection selectedRows = billList.getSelectedRows();
        Object[] primaryKeyValues = selectedRows.getPrimaryKeyValues();
        QFilter idFilter = new QFilter("id", QCP.equals,primaryKeyValues[0]);

        DynamicObject targetOrder = BusinessDataServiceHelper.loadSingle("lqs0_order",
                "number,lqs0_status",new QFilter[]{idFilter});
        return targetOrder;
    }

    public boolean refusalOrNo(DynamicObject targetOrder){
        if (StringUtils.equals(targetOrder.get("lqs0_status").toString(),"8")){
            this.getView().showMessage("已成功拒单，无法再次接单奥");
            return true;
        }
        return false;
    }

}
