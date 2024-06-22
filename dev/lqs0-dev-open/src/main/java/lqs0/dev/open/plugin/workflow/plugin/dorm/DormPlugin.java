package lqs0.dev.open.plugin.workflow.plugin.dorm;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class DormPlugin extends AbstractBillPlugIn {
    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);

        String opKey = ((FormOperate)args.getSource()).getOperateKey();
        if (StringUtils.equals("distribution",opKey)){
            distribution(args);
        } else if (StringUtils.equals("oneclickallocation",opKey)){
            oneClickAllocation(args);
        }

    }


    private void distribution(BeforeDoOperationEventArgs args) {


        String number = args.getListSelectedData().get(0).getNumber();

        QFilter numberFilter = new QFilter("number", QCP.equals,number);
        DynamicObject dormApplys = BusinessDataServiceHelper.loadSingle("lqs0_dormitory_allocation",
                "number,lqs0_dorm_info,lqs0_applyuser" , new QFilter[]{numberFilter});

        QFilter dormFilter = new QFilter("id", QCP.equals,dormApplys.get("lqs0_dorm_info.id"));

        DynamicObject dorm = BusinessDataServiceHelper.loadSingle("lqs0_dormitory_info",
                "number,lqs0_allocation_status" , new QFilter[]{dormFilter});
        dorm.set("lqs0_allocation_status",2);
        SaveServiceHelper.saveOperate("lqs0_dormitory_info", new DynamicObject[]{dorm}, OperateOption.create());
        this.getView().updateView();
    }


    private void oneClickAllocation(BeforeDoOperationEventArgs args) {

    }


}
