package lqs0.dev.open.plugin.other;

import kd.bos.bill.AbstractBillPlugIn;
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


public class OrderListPlugin extends AbstractBillPlugIn {
    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs){
        super.beforeDoOperation(eventArgs);
        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();

        if (StringUtils.equals("openorderdetails",opKey)){
            openOrderDetails(eventArgs);
        }
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
}
