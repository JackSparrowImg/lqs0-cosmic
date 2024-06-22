package lqs0.dev.open.plugin.workflow.plugin.exam;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.list.BillList;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import scala.math.BigInt;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class ExamListFilterPlugin extends AbstractBillPlugIn {
    @Override
    public void beforeBindData(EventObject e) {
        super.beforeBindData(e);

        QFilter userIdFilter = new QFilter("creator.id", QCP.equals, RequestContext.get().getCurrUserId());
        DynamicObject[] examRecords = BusinessDataServiceHelper.load("lqs0_exam_records", "number,lqs0_exam_name", new QFilter[]{userIdFilter});

        List<BigInt> filter = new ArrayList<>();

        for(int i = 0; i < examRecords.length; i++){
            BigInt tmp = BigInt.apply(examRecords[i].getString("lqs0_exam_name.id"));
            filter.add(tmp);
        }

        QFilter qFilter = new QFilter("id", QCP.not_in,filter);
        BillList billList = this.getControl("lqs0_billlistap");
        billList.setFilter(qFilter);

    }
}
