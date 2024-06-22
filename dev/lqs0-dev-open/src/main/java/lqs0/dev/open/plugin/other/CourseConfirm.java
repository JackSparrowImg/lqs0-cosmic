package lqs0.dev.open.plugin.other;

import jdk.nashorn.internal.ir.RuntimeNode;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.BillList;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

import java.util.List;
import java.util.UUID;

public class CourseConfirm extends AbstractBillPlugIn {
    private static String LIST_KEY = "lqs0_billlistap";
    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs){
        super.beforeDoOperation(eventArgs);
        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();
        if (StringUtils.equals("selectcourse",opKey)){
            // 获取当前选中行的 lqs0_integerfield1 字段值
            BillList billList = this.getControl(LIST_KEY);
            ListSelectedRow listSelectedRow = billList.getCurrentSelectedRowInfo();
            Object primaryKey = listSelectedRow.getPrimaryKeyValue();
            QFilter idFilter = new QFilter("id", QCP.equals,primaryKey);
            DynamicObject chooseCourse = BusinessDataServiceHelper.loadSingle("lqs0_all_class",
                    "name,lqs0_orgfield.name,lqs0_textfield,lqs0_textfield1,lqs0_textfield2,lqs0_textfield4," +
                            "lqs0_userfield.name,lqs0_textfield3,lqs0_integerfield1,lqs0_textfield5,lqs0_combofield",new QFilter[]{idFilter});

            Object name = chooseCourse.get("name");  //课程名称
            Object orgfieldName = chooseCourse.get("lqs0_orgfield.name"); //所属学院
            Object lqs0_textfield = chooseCourse.get("lqs0_textfield"); //上课周期
            Object lqs0_textfield1 = chooseCourse.get("lqs0_textfield1"); // 星期
            Object lqs0_textfield2 = chooseCourse.get("lqs0_textfield2"); // 课节
            Object lqs0_textfield4 = chooseCourse.get("lqs0_textfield4"); //授课教室
            Object lqs0_userfieldName = chooseCourse.get("lqs0_userfield.name"); //授课教师
            Object lqs0_textfield3 = chooseCourse.get("lqs0_textfield3"); //学分
            Object lqs0_textfield5 = chooseCourse.get("lqs0_textfield5"); //是否已选
            Object lqs0_integerfield1 = chooseCourse.get("lqs0_integerfield1"); // 剩余容量
            Object lqs0_combofield = chooseCourse.get("lqs0_combofield"); // 课程类型

            if(null != chooseCourse){
                if (lqs0_integerfield1 instanceof Integer) {
                    Integer integerField1Value = (Integer) lqs0_integerfield1;
                    if (integerField1Value > 0) {
                        // 更改选课列表的信息，减少选课名额
                        chooseCourse.set("lqs0_integerfield1",integerField1Value - 1);
                        chooseCourse.set("lqs0_textfield5","已选");
                        OperationResult lqs0BilllistapResult = SaveServiceHelper.saveOperate("lqs0_all_class", new DynamicObject[]{chooseCourse}, OperateOption.create());

                        // 更改个人课程列表，增加选课信息
                        DynamicObject selfCourse = BusinessDataServiceHelper.newDynamicObject("lqs0_selfcourse");
                        selfCourse.set("billstatus","C");
                        selfCourse.set("creator", RequestContext.get().getCurrUserId());
                        selfCourse.set("lqs0_userfield", RequestContext.get().getCurrUserId());
                        String uniqueID = "jiji_" + UUID.randomUUID().toString().substring(0, 5);
                        selfCourse.set("billno",uniqueID); // 测试UUID的可用性
                        selfCourse.set("lqs0_basedatafield",chooseCourse);
                        OperationResult saveResult = SaveServiceHelper.saveOperate("lqs0_selfcourse", new DynamicObject[]{selfCourse}, OperateOption.create());
                        List<Object> successPkIds = saveResult.getSuccessPkIds();
                        System.out.println(successPkIds.get(0));

                        this.getView().showMessage("选课成功！");
                    } else {
                        this.getView().showMessage("课程已满，无法选择！");
                        eventArgs.setCancel(true);
                    }
                } else {
                    this.getView().showTipNotification("选择课程数据有误，请联系管理员");
                    eventArgs.setCancel(true);
                }
            }else{
                this.getView().showTipNotification("选择课程数据有误，请联系管理员");
                eventArgs.setCancel(true);
            }
            this.getView().showMessage("选课成功！");

        }else if(StringUtils.equals("refreshcourse",opKey)){
            refreshPage();
        }else if(StringUtils.equals("exitcourse",opKey)){
            //执行退课逻辑
            handleExitCourse(eventArgs);
        }
        refreshPage();
    }

    private void handleExitCourse(BeforeDoOperationEventArgs eventArgs) {
        BillList billList = this.getControl(LIST_KEY);
        ListSelectedRow listSelectedRow = billList.getCurrentSelectedRowInfo();
        Object primaryKey = listSelectedRow.getPrimaryKeyValue();
        QFilter idFilter = new QFilter("id", QCP.equals, primaryKey);
        DynamicObject chooseCourse = BusinessDataServiceHelper.loadSingle("lqs0_all_class",
                "name,lqs0_orgfield.name,lqs0_textfield,lqs0_textfield1,lqs0_textfield2,lqs0_textfield4," +
                        "lqs0_userfield.name,lqs0_textfield3,lqs0_integerfield1,lqs0_textfield5,lqs0_combofield", new QFilter[]{idFilter});

        if (chooseCourse == null) {
            this.getView().showTipNotification("选择课程数据有误，请联系管理员");
            eventArgs.setCancel(true);
            return;
        }

        Object lqs0_textfield5 = chooseCourse.get("lqs0_textfield5");

        if (!"已选".equals(lqs0_textfield5)) {
            this.getView().showMessage("课程未选，无法退课！");
            eventArgs.setCancel(true);
            return;
        }

        Object lqs0_integerfield1 = chooseCourse.get("lqs0_integerfield1");

        if (lqs0_integerfield1 instanceof Integer) {
            Integer integerField1Value = (Integer) lqs0_integerfield1;
            chooseCourse.set("lqs0_integerfield1", integerField1Value + 1);
            chooseCourse.set("lqs0_textfield5", "");
            SaveServiceHelper.saveOperate("lqs0_all_class", new DynamicObject[]{chooseCourse}, OperateOption.create());

            // 删除个人课程列表中的对应课程
            QFilter userFilter = new QFilter("creator", QCP.equals, RequestContext.get().getCurrUserId());
            QFilter courseFilter = new QFilter("lqs0_basedatafield", QCP.equals, primaryKey);
            DynamicObject[] selfCourses = BusinessDataServiceHelper.load("lqs0_selfcourse", "id", new QFilter[]{userFilter, courseFilter});
            if (selfCourses != null && selfCourses.length > 0) {
                DeleteServiceHelper.delete("lqs0_selfcourse", new QFilter[]{userFilter, courseFilter});
            }
            this.getView().showMessage("退课成功！");
        } else {
            this.getView().showTipNotification("选择课程数据有误，请联系管理员");
            eventArgs.setCancel(true);
        }
        refreshPage();
    }

    public void refreshPage(){
        BillList billList = this.getControl(LIST_KEY);
        billList.refresh();
        billList.refreshData();
    }

}
