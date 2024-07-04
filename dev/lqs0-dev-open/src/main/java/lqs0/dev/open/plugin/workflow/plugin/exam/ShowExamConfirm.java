package lqs0.dev.open.plugin.workflow.plugin.exam;

import com.google.j2objc.annotations.ObjectiveCName;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListField;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.entity.datamodel.events.PackageDataEvent;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.OperationColumn;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.operatecol.OperationColItem;
import kd.bos.list.BillList;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.Date;
import java.util.EventObject;
import java.util.List;

public class ShowExamConfirm extends AbstractBillPlugIn {


    private static String LIST_KEY = "lqs0_billlistap";

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);

        String opKey = ((FormOperate) args.getSource()).getOperateKey();
        if (StringUtils.equals("examconfirm", opKey)) {
            examConfirm(args);
        } else if (StringUtils.equals("refresh", opKey)) {
            BillList billList = this.getControl(LIST_KEY);
            billList.refresh();
            billList.refreshData();
        }
    }

    private void examConfirm(BeforeDoOperationEventArgs args) {

        //动态表单获取当前考试
        BillList billList = this.getControl(LIST_KEY);
        ListSelectedRowCollection selectedRows = billList.getSelectedRows();

        Object[] primaryKeyValues = selectedRows.getPrimaryKeyValues();

        QFilter idFilter = new QFilter("id", QCP.equals, primaryKeyValues[0]);
        DynamicObject examInfo = BusinessDataServiceHelper.loadSingle("lqs0_exam",
                "number,name,lqs0_class_name,lqs0_teacher,lqs0_exam_time_end" +
                        ",lqs0_exam_time", new QFilter[]{idFilter});

        Object lqs0ExamTimeEnd = examInfo.get("lqs0_exam_time_end");

        Date date = new Date();
        long a = date.getTime();

        Date date1 = (Date) lqs0ExamTimeEnd;
        long examEndTime = date1.getTime();


        if (examEndTime < a) {
            this.getView().showMessage("考试时间已过，不可进入考试");
        } else {
            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.setFormId("lqs0_confirm_exam");  // 打开考试确认页面
            formShowParameter.setCustomParam("examId", primaryKeyValues[0]);
            formShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            this.getView().showForm(formShowParameter);
        }
    }
}
