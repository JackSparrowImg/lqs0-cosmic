package lqs0.dev.open.plugin.workflow.plugin.exam;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.ext.form.control.CountDown;
import kd.bos.form.*;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import java.util.*;

public class OnlineExamPageOtherPlugin extends AbstractFormPlugin {

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);

        String opKey = ((FormOperate) args.getSource()).getOperateKey();
        if (StringUtils.equals("saveexam", opKey)) {
            saveExam(args);
        } else if (StringUtils.equals("submitexam", opKey)) {
            submitConfirm(args);
        }

    }

    private void submitConfirm(BeforeDoOperationEventArgs e) {
        String msg = "确认提交试卷？";
        String detail = "";//请确保所有题目作答完成...
        // 消息框按钮类型
        // None:不启用  Toast:简短提示,几秒后消失
        MessageBoxOptions options = MessageBoxOptions.YesNo;
        // 确认提示类型
        // Default:默认提示  Save:保存提交类提示  Delete:删除类提示  Wait:等待类提示  Fail:失败类提示
        ConfirmTypes confirmTypes = ConfirmTypes.Save;
        // 确认框回调
        ConfirmCallBackListener callBack = new ConfirmCallBackListener("SUBMIT_CONFIRM", this);
        Map<Integer, String> btnNameMaps = new HashMap();
        btnNameMaps.put(6, "确认");
        btnNameMaps.put(7, "取消");
        // 用户自定义参数,前端会在afterConfirm中返回
        this.getView().showConfirm(msg,detail,options, confirmTypes, callBack, btnNameMaps);
    }

    @Override
    public void confirmCallBack(MessageBoxClosedEvent evt) {
        // 回调ID
        String callBackId = evt.getCallBackId();
        // 消息选择结果
        MessageBoxResult messageboxResult = evt.getResult();
        // 提示消息内容
        String tips = new String("提交成功");
        if (StringUtils.equalsIgnoreCase("SUBMIT_CONFIRM", callBackId) && messageboxResult.name().equalsIgnoreCase("Yes")) {
            //核心业务
            submitExam();
            getView().showSuccessNotification(tips);

            //关闭父页面
            IFormView parentView = this.getView().getParentView();
            parentView.invokeOperation("close");
            this.getView().sendFormAction(parentView);

            /*this.getView().returnDataToParent("close");*/

            this.getView().close();
        }
        super.confirmCallBack(evt);
    }

    @Override
    public void beforeClosed(BeforeClosedEvent e) {
        super.beforeClosed(e);
        IFormView parentView = this.getView().getParentView();
        parentView.invokeOperation("close");
        this.getView().sendFormAction(parentView);
    }

    private void submitExam() {


        DynamicObject selfExam = BusinessDataServiceHelper.newDynamicObject("lqs0_exam_records");
        DynamicObject examInfo = getCurrentExamInfo();

        String uniqueID = "jiji_record_" + UUID.randomUUID().toString().substring(0, 5);
        selfExam.set("number",uniqueID);
        selfExam.set("status","C");
        selfExam.set("enable",1);
        selfExam.set("creator", RequestContext.get().getCurrUserId());
        selfExam.set("lqs0_issubmit","C");

        CountDown countdown = this.getView().getControl("lqs0_countdownap");

        selfExam.set("lqs0_remain_time",examInfo.get("lqs0_exam_time"));
        selfExam.set("lqs0_exam_name",examInfo);
        selfExam.set("lqs0_classname",examInfo.get("lqs0_class_name"));
        selfExam.set("lqs0_commit_time",new Date());


        //单选题
        DynamicObjectCollection singleChoices = examInfo.getDynamicObjectCollection("lqs0_single_choice");
        DynamicObjectCollection singleChoicesTemp = selfExam.getDynamicObjectCollection("lqs0_single_choice");
        for (int i = 0; i < singleChoices.size(); i++){
            singleChoicesTemp.addNew();
            singleChoicesTemp.get(i).set("lqs0_single_choice_title",singleChoices.get(i).get("lqs0_single_choice_title"));
            singleChoicesTemp.get(i).set("lqs0_single_choice_a",singleChoices.get(i).get("lqs0_single_choice_a"));
            singleChoicesTemp.get(i).set("lqs0_single_choice_b",singleChoices.get(i).get("lqs0_single_choice_b"));
            singleChoicesTemp.get(i).set("lqs0_single_choice_c",singleChoices.get(i).get("lqs0_single_choice_c"));
            singleChoicesTemp.get(i).set("lqs0_single_choice_d",singleChoices.get(i).get("lqs0_single_choice_d"));
        }
        selfExam.set("lqs0_single_choice",singleChoicesTemp);


        //多选题
        DynamicObjectCollection multipleChoices = examInfo.getDynamicObjectCollection("lqs0_multiple_choice");
        DynamicObjectCollection multipleChoicesTemp = selfExam.getDynamicObjectCollection("lqs0_multiple_choice");
        for (int i = 0; i < multipleChoices.size(); i++){
            multipleChoicesTemp.addNew();
            multipleChoicesTemp.get(i).set("lqs0_multiple_choice_t",multipleChoices.get(i).get("lqs0_multiple_choice_t"));
            multipleChoicesTemp.get(i).set("lqs0_multiple_choice_a",multipleChoices.get(i).get("lqs0_multiple_choice_a"));
            multipleChoicesTemp.get(i).set("lqs0_multiple_choice_b",multipleChoices.get(i).get("lqs0_multiple_choice_b"));
            multipleChoicesTemp.get(i).set("lqs0_multiple_choice_c",multipleChoices.get(i).get("lqs0_multiple_choice_c"));
            multipleChoicesTemp.get(i).set("lqs0_multiple_choice_d",multipleChoices.get(i).get("lqs0_multiple_choice_d"));
        }
        selfExam.set("lqs0_multiple_choice",multipleChoicesTemp);

        //判断题
        DynamicObjectCollection trueorfalses = examInfo.getDynamicObjectCollection("lqs0_trueorfalse");
        DynamicObjectCollection trueorfalseTemp = selfExam.getDynamicObjectCollection("lqs0_trueorfalse");
        for (int i = 0; i < trueorfalses.size(); i++){
            trueorfalseTemp.addNew();
            trueorfalseTemp.get(i).set("lqs0_trueorfalse_title",trueorfalses.get(i).get("lqs0_trueorfalse_title"));
        }
        selfExam.set("lqs0_trueorfalse",trueorfalseTemp);

        //问答题
        DynamicObjectCollection qandas = examInfo.getDynamicObjectCollection("lqs0_qanda");
        DynamicObjectCollection qandaTemp = selfExam.getDynamicObjectCollection("lqs0_qanda");
        IPageCache iPageCache =this.getPageCache();
        for (int i = 0; i < qandas.size(); i++){
            qandaTemp.addNew();
            qandaTemp.get(i).set("lqs0_qanda_title",qandas.get(i).get("lqs0_qanda_title"));
        }
        selfExam.set("lqs0_qanda",qandaTemp);

        //给学生添加正确答案及判题
        submitJudgment(examInfo,selfExam);

        OperationResult saveResult = SaveServiceHelper.saveOperate("lqs0_exam_records", new DynamicObject[]{selfExam}, OperateOption.create());
    }

    private void submitJudgment(DynamicObject examInfo, DynamicObject selfExam) {

        DynamicObjectCollection singleChoices = examInfo.getDynamicObjectCollection("lqs0_single_choice");
        DynamicObjectCollection singleChoicesTemp = selfExam.getDynamicObjectCollection("lqs0_single_choice");
        IPageCache iPageCache =this.getPageCache();
        for (int i = 0; i < singleChoices.size(); i++){
            String value = iPageCache.get("lqs0_radiogroup_dan_" + i);
            singleChoicesTemp.get(i).set("lqs0_single_remark","正确答案：" + singleChoices.get(i).get("lqs0_single_right").toString());
            if(value == null){
                singleChoicesTemp.get(i).set("lqs0_single_isright","未作答" + "❌");
            } else if(value.equalsIgnoreCase(singleChoices.get(i).get("lqs0_single_right").toString())){
                singleChoicesTemp.get(i).set("lqs0_single_isright",value + "✅");
            }else{
                singleChoicesTemp.get(i).set("lqs0_single_isright",value + "❌");
            }

        }
        selfExam.set("lqs0_single_choice",singleChoicesTemp);


        //多选题
        DynamicObjectCollection multipleChoices = examInfo.getDynamicObjectCollection("lqs0_multiple_choice");
        DynamicObjectCollection multipleChoicesTemp = selfExam.getDynamicObjectCollection("lqs0_multiple_choice");
        for (int i = 0; i < multipleChoices.size(); i++){

            String value1 = iPageCache.get("lqs0_radiogroup_duo1_" + i);
            String value2 = iPageCache.get("lqs0_radiogroup_duo2_" + i);
            String value3 = iPageCache.get("lqs0_radiogroup_duo3_" + i);
            String value4 = iPageCache.get("lqs0_radiogroup_duo4_" + i);

            char[] rightAns = multipleChoices.get(i).get("lqs0_multiple_right").toString().toLowerCase().toString().replace(",","").toCharArray();
            StringBuilder ans = new StringBuilder();
            if (value1 != null) ans.append(value1);  if (value2 != null) ans.append(value2);
            if (value3 != null) ans.append(value3);  if (value4 != null) ans.append(value4);
            char[] myAns = ans.toString().toLowerCase().toCharArray();

            Arrays.sort(myAns);
            Arrays.sort(rightAns);

            multipleChoicesTemp.get(i).set("lqs0_multiple_remark","正确答案：" + multipleChoices.get(i).get("lqs0_multiple_right").toString());
            if(myAns.length == 0){
                multipleChoicesTemp.get(i).set("lqs0_multiple_isright","未作答" + "❌");
            } else if(Arrays.equals(myAns,rightAns)){
                multipleChoicesTemp.get(i).set("lqs0_multiple_isright",ans + "✅");
            }else{
                multipleChoicesTemp.get(i).set("lqs0_multiple_isright",ans + "❌");
            }

        }
        selfExam.set("lqs0_multiple_choice",multipleChoicesTemp);

        //判断题
        DynamicObjectCollection trueorfalses = examInfo.getDynamicObjectCollection("lqs0_trueorfalse");
        DynamicObjectCollection trueorfalseTemp = selfExam.getDynamicObjectCollection("lqs0_trueorfalse");
        for (int i = 0; i < trueorfalses.size(); i++){
            String myAns = iPageCache.get("lqs0_radiogroup_pan_" + i);
            String rightAns = trueorfalses.get(i).get("lqs0_trueorfalse_right").toString();

            trueorfalseTemp.get(i).set("lqs0_tof_remark","正确答案：" + rightAns);
            if(myAns == null){
                trueorfalseTemp.get(i).set("lqs0_tof_isright","未作答" + "❌");
            } else if((myAns.equalsIgnoreCase("1") && rightAns.equalsIgnoreCase("对")) ||
               (myAns.equalsIgnoreCase("0") && rightAns.equalsIgnoreCase("错"))
            ){
                trueorfalseTemp.get(i).set("lqs0_tof_isright",rightAns + "✅");
            }else{
                if(myAns.equalsIgnoreCase("0")){
                    trueorfalseTemp.get(i).set("lqs0_tof_isright","错" + "❌");
                }else{
                    trueorfalseTemp.get(i).set("lqs0_tof_isright","对" + "❌");
                }

            }
        }
        selfExam.set("lqs0_trueorfalse",trueorfalseTemp);

        //问答题
        DynamicObjectCollection qandas = examInfo.getDynamicObjectCollection("lqs0_qanda");
        DynamicObjectCollection qandaTemp = selfExam.getDynamicObjectCollection("lqs0_qanda");
        for (int i = 0; i < qandas.size(); i++){
            String value = iPageCache.get("lqs0_wen_ans_" + i);
            qandaTemp.get(i).set("lqs0_qanda_remark","正确答案：" + qandas.get(i).get("lqs0_wen_right"));
            if(value == null){
                qandaTemp.get(i).set("lqs0_qanda_isright","未作答");
            } else{
                qandaTemp.get(i).set("lqs0_qanda_isright",value);
            }

        }
        selfExam.set("lqs0_qanda",qandaTemp);

    }


    private void saveExam(BeforeDoOperationEventArgs args) {

    }

    public DynamicObject getCurrentExamInfo(){
        Object examId = this.getView().getFormShowParameter().getCustomParam("examId");
        if(null == examId) {
            return null;
        }
        QFilter numberFilter = new QFilter("id", QCP.equals,examId);
        DynamicObject examInfo = BusinessDataServiceHelper.loadSingle("lqs0_exam",
                "number,name,lqs0_class_name,lqs0_teacher,lqs0_exam_time_end,lqs0_exam_time," +
                        "lqs0_single_choice,lqs0_multiple_choice,lqs0_trueorfalse,lqs0_qanda," +
                        "lqs0_single_choice_title,lqs0_single_choice_a,lqs0_single_choice_b,lqs0_single_choice_c,lqs0_single_choice_d," +
                        "lqs0_multiple_choice_t,lqs0_multiple_choice_a,lqs0_multiple_choice_b,lqs0_multiple_choice_c,lqs0_multiple_choice_d," +
                        "lqs0_trueorfalse_title" +
                        ",lqs0_qanda_title" +
                        ",lqs0_single_right,lqs0_multiple_right,lqs0_trueorfalse_right,lqs0_wen_right",
                new QFilter[]{numberFilter});
        return examInfo;
    }
}
