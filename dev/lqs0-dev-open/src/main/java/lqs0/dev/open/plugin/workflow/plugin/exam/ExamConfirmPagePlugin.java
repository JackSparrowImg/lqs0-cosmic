package lqs0.dev.open.plugin.workflow.plugin.exam;

import com.kingdee.cosmic.ctrl.swing.StringUtils;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.CloseCallBack;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.EventObject;

public class ExamConfirmPagePlugin extends AbstractFormPlugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        Object examId = this.getView().getFormShowParameter().getCustomParam("examId");
        QFilter idFilter = new QFilter("id", QCP.equals,examId);
        DynamicObject examInfo = BusinessDataServiceHelper.loadSingle("lqs0_exam",
                "number,name,lqs0_class_name,lqs0_teacher,lqs0_exam_time_end" +
                        ",lqs0_exam_time" , new QFilter[]{idFilter});

        if(null != examId){
            this.getModel().setValue("lqs0_exam_number",examId);
            Object a = examInfo.get("lqs0_class_name.lqs0_userfield.name");
            String teacherName = a.toString();
            //this.getModel().setValue("lqs0_class_teacher",teacherName);
        }
    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        this.addClickListeners("btnok");
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);

        Control source = (Control) evt.getSource();
        if(StringUtils.equals("btnok",source.getKey())){

            Object examId = this.getView().getFormShowParameter().getCustomParam("examId");
            FormShowParameter formShowParameter = new FormShowParameter();

           // formShowParameter.setFormId("lqs0_online_exam");  // 打开在线考试

            formShowParameter.setFormId("lqs0_exam_face_identify"); //打开人脸识别界面

            formShowParameter.setCustomParam("examId",examId);
            formShowParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
            this.getView().showForm(formShowParameter);
        }
    }

    //父页面监听子页面状态，如果子页面关闭，父页面同样关闭
    @Override
    public void closedCallBack(ClosedCallBackEvent closedCallBackEvent) {
        super.closedCallBack(closedCallBackEvent);
        if (StringUtils.equals(closedCallBackEvent.getActionId(), "CLOSECALLBACK")) {
            String data = (String) closedCallBackEvent.getReturnData();
            if (data != null && !data.isEmpty() && "close".equals(data)) {//返回字符串为close时
                this.getView().close();
            }
        }
    }
}
