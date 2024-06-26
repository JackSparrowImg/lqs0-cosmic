package lqs0.dev.open.plugin.workflow.plugin.exam;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.ext.form.control.CountDown;
import kd.bos.ext.form.control.events.CountDownEvent;
import kd.bos.ext.form.control.events.CountDownListener;
import kd.bos.form.IFormView;
import kd.bos.form.IPageCache;
import kd.bos.form.container.Tab;
import kd.bos.form.control.Label;
import kd.bos.form.control.events.BeforeClickEvent;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.field.ComboEdit;
import kd.bos.form.field.RadioEdit;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import java.util.EventObject;


public class OlineExamPagePlugin extends AbstractFormPlugin implements CountDownListener {
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);

        DynamicObject examInfo = getCurrentExamInfo();

        //绑定考试信息
        Object className = examInfo.get("lqs0_class_name.id");
        String teacherName = examInfo.get("lqs0_class_name.lqs0_userfield.name").toString();
        Object examTime = examInfo.get("lqs0_exam_time");

        this.getModel().setValue("lqs0_exam_status","考试中");

        String userName = RequestContext.get().getUserName(); // 获取当前用户名

        this.getModel().setValue("lqs0_stu", userName);
        this.getModel().setValue("lqs0_class_name",className);

        //绑定答题信息
        // 1. 查询出单选题
        DynamicObjectCollection singleChoices = examInfo.getDynamicObjectCollection("lqs0_single_choice");
        DynamicObjectCollection multipleChoices = examInfo.getDynamicObjectCollection("lqs0_multiple_choice");
        DynamicObjectCollection trueorfalses =  examInfo.getDynamicObjectCollection("lqs0_trueorfalse");
        DynamicObjectCollection qandas =  examInfo.getDynamicObjectCollection("lqs0_qanda");


        /**
         *  初始化构造答题区域
         *
         *  */
        int dan = 0,duo = 0,pan = 0,wen = 0;
        IPageCache iPageCache =this.getPageCache();
        iPageCache.put("dan",String.valueOf(dan));
        iPageCache.put("duo",String.valueOf(duo));
        iPageCache.put("pan",String.valueOf(pan));
        iPageCache.put("wen",String.valueOf(wen));


        if (singleChoices.size() > 0){
            this.getModel().setValue("lqs0_dan_title",singleChoices.get(dan).getString("lqs0_single_choice_title"));
            Label labelA = this.getView().getControl("lqs0_dan_title_a");
            labelA.setText(singleChoices.get(dan).getString("lqs0_single_choice_a"));

            Label labelB = this.getView().getControl("lqs0_dan_title_b");
            labelB.setText(singleChoices.get(dan).getString("lqs0_single_choice_b"));

            Label labelC = this.getView().getControl("lqs0_dan_title_c");
            labelC.setText(singleChoices.get(dan).getString("lqs0_single_choice_c"));

            Label labelD = this.getView().getControl("lqs0_dan_title_d");
            labelD.setText(singleChoices.get(dan).getString("lqs0_single_choice_d"));
        }
        if (multipleChoices.size() > 0){
            this.getModel().setValue("lqs0_duo_title",multipleChoices.get(dan).getString("lqs0_multiple_choice_t"));
            Label labelA = this.getView().getControl("lqs0_duo_title_a");
            labelA.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_a"));

            Label labelB = this.getView().getControl("lqs0_duo_title_b");
            labelB.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_b"));

            Label labelC = this.getView().getControl("lqs0_duo_title_c");
            labelC.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_c"));

            Label labelD = this.getView().getControl("lqs0_duo_title_d");
            labelD.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_d"));
        }
        if(trueorfalses.size() > 0){
            this.getModel().setValue("lqs0_pan_title",trueorfalses.get(pan).getString("lqs0_trueorfalse_title"));
        }
        if (qandas.size() > 0){
            this.getModel().setValue("lqs0_wen_title",qandas.get(wen).getString("lqs0_qanda_title"));
        }



        //绑定倒计时信息
        CountDown countdown = this.getView().getControl("lqs0_countdownap");
        int time = Integer.parseInt(examTime.toString());
        countdown.setDuration(time);
        countdown.start();

    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        CountDown countdown = this.getView().getControl("lqs0_countdownap");
        countdown.addCountDownListener(this);
    }

    @Override
    public void onCountDownEnd(CountDownEvent evt) {
        CountDownListener.super.onCountDownEnd(evt);
        CountDown countDown = (CountDown) evt.getSource();
        if (countDown.getKey().equals("lqs0_countdownap")) {
            //TODO: 考试结束，自动提交
            this.getView().showMessage("考试时间已到，试卷已自动提交！");
        }
    }


    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);

        String opKey = ((FormOperate)args.getSource()).getOperateKey();
        if (StringUtils.equals("lastquestion",opKey)){
            lastQuestion(args);
        }else if (StringUtils.equals("nextquestion",opKey)){
            nextQuestion(args);
        }

    }

    @Override
    public void propertyChanged(PropertyChangedArgs e) {
        super.propertyChanged(e);
        IPageCache iPageCache = this.getPageCache();
        String operationName = e.getProperty().getName();
        if (this.getModel().getValue("lqs0_radiogroup_dan") != null
                && operationName.equalsIgnoreCase("lqs0_radiogroup_dan")){
            int dan = Integer.parseInt(iPageCache.get("dan"));
            String value = (String) this.getModel().getValue("lqs0_radiogroup_dan");
            iPageCache.put("lqs0_radiogroup_dan_" + dan, value);
        }else if (this.getModel().getValue("lqs0_radiogroup_duo1") != null
                && operationName.equalsIgnoreCase("lqs0_radiogroup_duo1")){
            int duo = Integer.parseInt(iPageCache.get("duo"));
            String value = (String) this.getModel().getValue("lqs0_radiogroup_duo1");
            iPageCache.put("lqs0_radiogroup_duo1_" + duo, value);
        }else if (this.getModel().getValue("lqs0_radiogroup_duo2") != null
                && operationName.equalsIgnoreCase("lqs0_radiogroup_duo2")){
            int duo = Integer.parseInt(iPageCache.get("duo"));
            String value = (String) this.getModel().getValue("lqs0_radiogroup_duo2");
            iPageCache.put("lqs0_radiogroup_duo2_" + duo, value);
        }else if (this.getModel().getValue("lqs0_radiogroup_duo3") != null
                && operationName.equalsIgnoreCase("lqs0_radiogroup_duo3")){
            int duo = Integer.parseInt(iPageCache.get("duo"));
            String value = (String) this.getModel().getValue("lqs0_radiogroup_duo3");
            iPageCache.put("lqs0_radiogroup_duo3_" + duo, value);
        }else if (this.getModel().getValue("lqs0_radiogroup_duo4") != null
                && operationName.equalsIgnoreCase("lqs0_radiogroup_duo4")){
            int duo = Integer.parseInt(iPageCache.get("duo"));
            String value = (String) this.getModel().getValue("lqs0_radiogroup_duo4");
            iPageCache.put("lqs0_radiogroup_duo4_" + duo, value);
        }else if (this.getModel().getValue("lqs0_radiogroup_pan") != null
                && operationName.equalsIgnoreCase("lqs0_radiogroup_pan")){
            int pan = Integer.parseInt(iPageCache.get("pan"));
            String value = (String) this.getModel().getValue("lqs0_radiogroup_pan");
            iPageCache.put("lqs0_radiogroup_pan_" + pan, value);
        }else if (this.getModel().getValue("lqs0_wen_ans") != null
                && operationName.equalsIgnoreCase("lqs0_wen_ans")){
            int wen = Integer.parseInt(iPageCache.get("wen"));
            String value = (String) this.getModel().getValue("lqs0_wen_ans");
            iPageCache.put("lqs0_wen_ans_" + wen,value);
        }
    }

    private void nextQuestion(BeforeDoOperationEventArgs args) {
        Tab tab = this.getView().getControl("lqs0_tabap");
        String currentTab = tab.getCurrentTab();
        switch (currentTab){
            case "lqs0_dan": danNext();break;
            case "lqs0_duo": duoNext();break;
            case "lqs0_pan": panNext();break;
            case "lqs0_wen": wenNext();break;
            default: break;
        }
    }
    private void danNext() {

        IPageCache iPageCache =this.getPageCache();
        int dan = Integer.parseInt(iPageCache.get("dan"));


        //  判断当前题目是否有答案
        // 1. 如果有答案，那么单选控件将有值，我们将该值保存入页面缓存，并将单选控件的值设为空
        String value = (String) this.getModel().getValue("lqs0_radiogroup_dan");
        iPageCache.put("lqs0_radiogroup_dan_" + dan, value);

        DynamicObject examInfo = getCurrentExamInfo();
        if(examInfo == null) return;
        DynamicObjectCollection singleChoices = examInfo.getDynamicObjectCollection("lqs0_single_choice");


        if (singleChoices.size()  > dan + 1){

            dan += 1;
            iPageCache.put("dan", String.valueOf(dan));

            //如果还有下一题，则将单选按钮组的值设置为空
            this.getModel().setValue("lqs0_radiogroup_dan",null);

            //判断当前题目是否有缓存，如果缓存中有该题目答案，那么将缓存取出来
            String cacheValue = iPageCache.get("lqs0_radiogroup_dan_" + dan);
            if(null != cacheValue){
                this.getModel().setValue("lqs0_radiogroup_dan",cacheValue);
            }


            this.getModel().setValue("lqs0_dan_title",singleChoices.get(dan).getString("lqs0_single_choice_title"));
            Label labelA = this.getView().getControl("lqs0_dan_title_a");
            labelA.setText(singleChoices.get(dan).getString("lqs0_single_choice_a"));

            Label labelB = this.getView().getControl("lqs0_dan_title_b");
            labelB.setText(singleChoices.get(dan).getString("lqs0_single_choice_b"));

            Label labelC = this.getView().getControl("lqs0_dan_title_c");
            labelC.setText(singleChoices.get(dan).getString("lqs0_single_choice_c"));

            Label labelD = this.getView().getControl("lqs0_dan_title_d");
            labelD.setText(singleChoices.get(dan).getString("lqs0_single_choice_d"));

        }else{
            this.getView().showMessage("已经到是单选最后一题啦！");
        }



    }
    private void duoNext() {


        IPageCache iPageCache =this.getPageCache();
        int duo = Integer.parseInt(iPageCache.get("duo"));

        //  判断当前题目是否有答案
        // 1. 如果有答案，那么单选控件将有值，我们将该值保存入页面缓存，并将单选控件的值设为空
        String value1 = (String) this.getModel().getValue("lqs0_radiogroup_duo1");
        iPageCache.put("lqs0_radiogroup_duo1_" + duo, value1);

        String value2 = (String) this.getModel().getValue("lqs0_radiogroup_duo2");
        iPageCache.put("lqs0_radiogroup_duo2_" + duo, value2);

        String value3 = (String) this.getModel().getValue("lqs0_radiogroup_duo3");
        iPageCache.put("lqs0_radiogroup_duo3_" + duo, value3);

        String value4 = (String) this.getModel().getValue("lqs0_radiogroup_duo4");
        iPageCache.put("lqs0_radiogroup_duo4_" + duo, value4);

        DynamicObject examInfo = getCurrentExamInfo();
        if(null == examInfo) return;
        DynamicObjectCollection multipleChoices = examInfo.getDynamicObjectCollection("lqs0_multiple_choice");



        if (multipleChoices.size()  > duo + 1){

            duo += 1;
            iPageCache.put("duo", String.valueOf(duo));

            //如果还有下一题，则将单选按钮组的值设置为空
            this.getModel().setValue("lqs0_radiogroup_duo1",null);
            this.getModel().setValue("lqs0_radiogroup_duo2",null);
            this.getModel().setValue("lqs0_radiogroup_duo3",null);
            this.getModel().setValue("lqs0_radiogroup_duo4",null);

            String cacheValue1 = iPageCache.get("lqs0_radiogroup_duo1_" + duo);
            if (null != cacheValue1)
                this.getModel().setValue("lqs0_radiogroup_duo1",cacheValue1);

            String cacheValue2 = iPageCache.get("lqs0_radiogroup_duo2_" + duo);
            if (null != cacheValue2)
                this.getModel().setValue("lqs0_radiogroup_duo2",cacheValue2);

            String cacheValue3 = iPageCache.get("lqs0_radiogroup_duo3_" + duo);
            if (null != cacheValue3)
                this.getModel().setValue("lqs0_radiogroup_duo3",cacheValue3);

            String cacheValue4 = iPageCache.get("lqs0_radiogroup_duo4_" + duo);
            if (null != cacheValue4)
                this.getModel().setValue("lqs0_radiogroup_duo4",cacheValue4);


            this.getModel().setValue("lqs0_duo_title",multipleChoices.get(duo).getString("lqs0_multiple_choice_t"));
            Label labelA = this.getView().getControl("lqs0_duo_title_a");
            labelA.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_a"));

            Label labelB = this.getView().getControl("lqs0_duo_title_b");
            labelB.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_b"));

            Label labelC = this.getView().getControl("lqs0_duo_title_c");
            labelC.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_c"));

            Label labelD = this.getView().getControl("lqs0_duo_title_d");
            labelD.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_d"));

        }else{
            this.getView().showMessage("已经到是多选最后一题啦！");
        }
    }

    private void panNext() {
        DynamicObject examInfo = getCurrentExamInfo();
        if(null == examInfo) {
            return;
        }
        DynamicObjectCollection trueorfalses = examInfo.getDynamicObjectCollection("lqs0_trueorfalse");

        IPageCache iPageCache =this.getPageCache();

        int pan = Integer.parseInt(iPageCache.get("pan"));

        String value = (String) this.getModel().getValue("lqs0_radiogroup_pan");
        iPageCache.put("lqs0_radiogroup_pan_" + pan, value);


        if(trueorfalses.size() > pan + 1){

            pan += 1;
            iPageCache.put("pan", String.valueOf(pan));
            //如果还有下一题，则将单选按钮组的值设置为空
            this.getModel().setValue("lqs0_radiogroup_pan",null);

            String cacheValue = iPageCache.get("lqs0_radiogroup_pan_" + pan);
            if (null != cacheValue)
                this.getModel().setValue("lqs0_radiogroup_pan",cacheValue);

            this.getModel().setValue("lqs0_pan_title",trueorfalses.get(pan).getString("lqs0_trueorfalse_title"));

        }else{
            this.getView().showMessage("已经是判断最后一题啦！");
        }
    }

    private void wenNext() {
        DynamicObject examInfo = getCurrentExamInfo();
        if(null == examInfo) {
            return;
        }

        DynamicObjectCollection qandas =  examInfo.getDynamicObjectCollection("lqs0_qanda");

        IPageCache iPageCache =this.getPageCache();
        int wen = Integer.parseInt(iPageCache.get("wen"));

        String value = (String) this.getModel().getValue("lqs0_wen_ans");
        iPageCache.put("lqs0_wen_ans_" + wen,value);

        if(qandas.size() > wen + 1){

            wen += 1;
            iPageCache.put("pan", String.valueOf(wen));
            this.getModel().setValue("lqs0_wen_ans",null);

            String cacheValue = iPageCache.get("lqs0_wen_ans_" + wen);
            this.getModel().setValue("lqs0_wen_ans",cacheValue);


            this.getModel().setValue("lqs0_wen_title",qandas.get(wen).getString("lqs0_qanda_title"));

        }else{
            this.getView().showMessage("已经是问答最后一题啦！");
        }
    }

    private void lastQuestion(BeforeDoOperationEventArgs args) {
        Tab tab = this.getView().getControl("lqs0_tabap");
        String currentTab = tab.getCurrentTab();
        switch (currentTab){
            case "lqs0_dan": danLast();break;
            case "lqs0_duo": duoLast();break;
            case "lqs0_pan": panLast();break;
            case "lqs0_wen": wenLast();break;
            default: break;
        }
    }

    private void danLast() {

        IPageCache iPageCache =this.getPageCache();
        int dan = Integer.parseInt(iPageCache.get("dan"));

        //  判断当前题目是否有答案
        // 1. 如果有答案，那么单选控件将有值，我们将该值保存入页面缓存，并将单选控件的值设为空
        String value = (String) this.getModel().getValue("lqs0_radiogroup_dan");
        iPageCache.put("lqs0_radiogroup_dan_" + dan, value);


        DynamicObject examInfo = getCurrentExamInfo();
        if(null == examInfo) {
            return;
        }
        DynamicObjectCollection singleChoices = examInfo.getDynamicObjectCollection("lqs0_single_choice");


        if (dan - 1 >= 0){

            dan -= 1;
            iPageCache.put("dan", String.valueOf(dan));

            this.getModel().setValue("lqs0_radiogroup_dan",null);


            //判断当前题目是否有缓存，如果缓存中有该题目答案，那么将缓存取出来
            String cacheValue = iPageCache.get("lqs0_radiogroup_dan_" + dan);
            if(null != cacheValue){
                this.getModel().setValue("lqs0_radiogroup_dan",cacheValue);
            }

            this.getModel().setValue("lqs0_dan_title",singleChoices.get(dan).getString("lqs0_single_choice_title"));
            Label labelA = this.getView().getControl("lqs0_dan_title_a");
            labelA.setText(singleChoices.get(dan).getString("lqs0_single_choice_a"));

            Label labelB = this.getView().getControl("lqs0_dan_title_b");
            labelB.setText(singleChoices.get(dan).getString("lqs0_single_choice_b"));

            Label labelC = this.getView().getControl("lqs0_dan_title_c");
            labelC.setText(singleChoices.get(dan).getString("lqs0_single_choice_c"));

            Label labelD = this.getView().getControl("lqs0_dan_title_d");
            labelD.setText(singleChoices.get(dan).getString("lqs0_single_choice_d"));
        }else{
            this.getView().showMessage("已经到是单选第一题啦！");
        }
    }

    private void duoLast() {

        IPageCache iPageCache =this.getPageCache();
        int duo = Integer.parseInt(iPageCache.get("duo"));

        DynamicObject examInfo = getCurrentExamInfo();
        DynamicObjectCollection multipleChoices = examInfo.getDynamicObjectCollection("lqs0_multiple_choice");

        //  判断当前题目是否有答案
        // 1. 如果有答案，那么单选控件将有值，我们将该值保存入页面缓存，并将单选控件的值设为空
        String value1 = (String) this.getModel().getValue("lqs0_radiogroup_duo1");
        iPageCache.put("lqs0_radiogroup_duo1_" + duo, value1);

        String value2 = (String) this.getModel().getValue("lqs0_radiogroup_duo2");
        iPageCache.put("lqs0_radiogroup_duo2_" + duo, value2);

        String value3 = (String) this.getModel().getValue("lqs0_radiogroup_duo3");
        iPageCache.put("lqs0_radiogroup_duo3_" + duo, value3);

        String value4 = (String) this.getModel().getValue("lqs0_radiogroup_duo4");
        iPageCache.put("lqs0_radiogroup_duo4_" + duo, value4);



        if (duo - 1 >= 0){

            duo -= 1;
            iPageCache.put("duo", String.valueOf(duo));

            this.getModel().setValue("lqs0_radiogroup_duo1",null);
            this.getModel().setValue("lqs0_radiogroup_duo2",null);
            this.getModel().setValue("lqs0_radiogroup_duo3",null);
            this.getModel().setValue("lqs0_radiogroup_duo4",null);




            String cacheValue1 = iPageCache.get("lqs0_radiogroup_duo1_" + duo);
            if (null != cacheValue1)
                this.getModel().setValue("lqs0_radiogroup_duo1",cacheValue1);

            String cacheValue2 = iPageCache.get("lqs0_radiogroup_duo2_" + duo);
            if (null != cacheValue2)
                this.getModel().setValue("lqs0_radiogroup_duo2",cacheValue2);

            String cacheValue3 = iPageCache.get("lqs0_radiogroup_duo3_" + duo);
            if (null != cacheValue3)
                this.getModel().setValue("lqs0_radiogroup_duo3",cacheValue3);

            String cacheValue4 = iPageCache.get("lqs0_radiogroup_duo4_" + duo);
            if (null != cacheValue4)
                this.getModel().setValue("lqs0_radiogroup_duo4",cacheValue4);

            this.getModel().setValue("lqs0_duo_title",multipleChoices.get(duo).getString("lqs0_multiple_choice_t"));
            Label labelA = this.getView().getControl("lqs0_duo_title_a");
            labelA.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_a"));

            Label labelB = this.getView().getControl("lqs0_duo_title_b");
            labelB.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_b"));

            Label labelC = this.getView().getControl("lqs0_duo_title_c");
            labelC.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_c"));

            Label labelD = this.getView().getControl("lqs0_duo_title_d");
            labelD.setText(multipleChoices.get(duo).getString("lqs0_multiple_choice_d"));

        }else{
            this.getView().showMessage("已经到是多选第一题啦！");
        }
    }

    private void panLast() {
        DynamicObject examInfo = getCurrentExamInfo();
        if(null == examInfo) {
            return;
        }
        DynamicObjectCollection trueorfalses = examInfo.getDynamicObjectCollection("lqs0_trueorfalse");

        IPageCache iPageCache =this.getPageCache();

        int pan = Integer.parseInt(iPageCache.get("pan"));

        String value = (String) this.getModel().getValue("lqs0_radiogroup_pan");
        iPageCache.put("lqs0_radiogroup_pan_" + pan, value);


        if(pan - 1 >= 0){

            pan -= 1;
            iPageCache.put("pan", String.valueOf(pan));
            this.getModel().setValue("lqs0_radiogroup_pan",null);

            String cacheValue = iPageCache.get("lqs0_radiogroup_pan_" + pan);
            if (null != cacheValue)
                this.getModel().setValue("lqs0_radiogroup_pan",cacheValue);

            this.getModel().setValue("lqs0_pan_title",trueorfalses.get(pan).getString("lqs0_trueorfalse_title"));

        }else{
            this.getView().showMessage("已经是判断第一题啦！");
        }
    }

    private void wenLast() {
        DynamicObject examInfo = getCurrentExamInfo();
        if(null == examInfo) {
            return;
        }
        DynamicObjectCollection qandas =  examInfo.getDynamicObjectCollection("lqs0_qanda");

        IPageCache iPageCache =this.getPageCache();
        int wen = Integer.parseInt(iPageCache.get("wen"));

        String value = (String) this.getModel().getValue("lqs0_wen_ans");
        iPageCache.put("lqs0_wen_ans_" + wen,value);



        if(wen - 1 >= 0){

            wen -= 1;
            iPageCache.put("wen", String.valueOf(wen));
            this.getModel().setValue("lqs0_wen_ans",null);

            String cacheValue = iPageCache.get("lqs0_wen_ans_" + wen);
            this.getModel().setValue("lqs0_wen_ans",cacheValue);

            this.getModel().setValue("lqs0_wen_title",qandas.get(wen).getString("lqs0_qanda_title"));

        }else{
            this.getView().showMessage("已经是问答第一题啦！");
        }
    }

    public DynamicObject getCurrentExamInfo(){
        Object examId = this.getView().getFormShowParameter().getCustomParam("examId");
        if(null == examId) {
            return null;
        }
        QFilter numberFilter = new QFilter("id", QCP.equals,examId);
        DynamicObject examInfo = BusinessDataServiceHelper.loadSingle("lqs0_exam",
                "number,name,lqs0_class_name,lqs0_teacher,lqs0_exam_time_end,lqs0_exam_time," +
                        "lqs0_single_choice,lqs0_multiple_choice," +
                        ",lqs0_qanda," +
                        "lqs0_single_choice_title,lqs0_single_choice_a,lqs0_single_choice_b,lqs0_single_choice_c,lqs0_single_choice_d," +
                        "lqs0_multiple_choice_t,lqs0_multiple_choice_a,lqs0_multiple_choice_b,lqs0_multiple_choice_c,lqs0_multiple_choice_d," +
                        "lqs0_trueorfalse_title" +
                        ",lqs0_qanda_title",
                new QFilter[]{numberFilter});
        return examInfo;
    }

}
