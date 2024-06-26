package lqs0.dev.open.plugin.other.app;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.ext.form.control.CountDown;
import kd.bos.ext.form.control.events.CountDownEvent;
import kd.bos.ext.form.control.events.CountDownListener;
import kd.bos.form.MobileFormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Label;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.plugin.AbstractMobFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import lqs0.dev.open.plugin.operate.PayMethod;
import java.util.*;



public class PayPagePulgin extends AbstractMobFormPlugin implements CountDownListener {


    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs){
        super.beforeDoOperation(eventArgs);

        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();

        if (StringUtils.equals("confirmpay",opKey)){
            ConfirmPay(eventArgs);
        }
    }

    private void ConfirmPay(BeforeDoOperationEventArgs eventArgs){





        //获取当前页面所选中的支付方式
        String value = (String) this.getModel().getValue("lqs0_radiogroupfield0");

        //将需要赋值的管理字传输到下一个页面
        Object shopId = this.getView().getFormShowParameter().getCustomParam("shopId");
        Map<String,String> lqs0Address = this.getView().getFormShowParameter().getCustomParam("addressId");
        Object allmoney = this.getView().getFormShowParameter().getCustomParam("allmoney");


        QFilter shopFilter =  new QFilter("id",QCP.equals,shopId);
        DynamicObject shop = BusinessDataServiceHelper.loadSingle("lqs0_shop","name",new QFilter[]{shopFilter});

        Object selfOrderId = this.getView().getFormShowParameter().getCustomParam("selfOrderId");

        QFilter idFilter =  new QFilter("id",QCP.equals,selfOrderId);
        DynamicObject selfOrder = BusinessDataServiceHelper.loadSingle
                ("lqs0_order", "number,name,lqs0_status,lqs0_pay_status," +
                        "lqs0_checkout_time,lqs0_pay_method,lqs0_pack_amount,lqs0_estimated_time", new QFilter[]{idFilter});

        Object orderId = selfOrder.getPkValue();

        PayMethod payMethod = new PayMethod();
        String result = payMethod.pay(value,allmoney,orderId,shop.get("name"));

        //显示支付成功页面
        /*MobileFormShowParameter showParameter = new MobileFormShowParameter();
        showParameter.setFormId("lqs0_commit_success");
        showParameter.getOpenStyle().setShowType(ShowType.Floating);
        showParameter.setCustomParam("orderId",orderId);
        this.getView().showForm(showParameter);*/

        //打开支付宝进行支付
        this.getView().openUrl(result);

        if(result != null){
            Date date = new Date();
            long time = date.getTime() + 60 * 60;
            selfOrder.set("lqs0_estimated_time",new Date(time)); //订单预计送达时间
            selfOrder.set("lqs0_status",2); // 设置订单状态，1：待付款，2：待接单，3：已结单，4：派送中，5：已完成，6：已取消,7: 已退款
            selfOrder.set("lqs0_pay_status",1); // 设置支付状态，1：已支付，2：未支付，3：退款
            selfOrder.set("lqs0_pay_method",value); // 设置支付方式，1：微信，2：支付宝，3：校园卡
            selfOrder.set("lqs0_checkout_time",new Date());
            OperationResult saveOperate = SaveServiceHelper.saveOperate("lqs0_order", new DynamicObject[]{selfOrder}, OperateOption.create());
            String message = saveOperate.getMessage(); //保存结果信息
            CountDown countdown = this.getView().getControl("lqs0_countdown");
            countdown.pause();
            this.getView().showMessage("支付成功");
        }else {
            this.getView().showMessage("支付失败");
        }

    }

    @Override
    public void afterBindData(EventObject e) {
        super.beforeBindData(e);
        CountDown countdown = this.getView().getControl("lqs0_countdown");

        countdown.setDuration(60 * 15);
        countdown.start();


        //设置支付金额
        Label dabao = this.getView().getControl("lqs0_allmoney");
        Object allmoney = this.getView().getFormShowParameter().getCustomParam("allmoney");
        double amount = Double.parseDouble(allmoney.toString());
        String  str = String.format("%.2f",amount);
        amount = Double.parseDouble(str);
        dabao.setText("" + amount);

        //设置店铺名称
        Label shopLabel = this.getView().getControl("lqs0_shopname");
        Object shopId = this.getView().getFormShowParameter().getCustomParam("shopId");
        QFilter idFilter = new QFilter("id", QCP.equals,shopId);

        DynamicObject shop = BusinessDataServiceHelper.loadSingle("lqs0_shop",
                "number,name",new QFilter[]{idFilter});
        Object shopname =  shop.get("name");
        shopLabel.setText(shopname.toString());
    }

    public void registerListener(EventObject e) {
        super.registerListener(e);
        CountDown countdown = this.getView().getControl("lqs0_countdown");
        countdown.addCountDownListener(this);
    }

    @Override
    public void onCountDownEnd(CountDownEvent evt) {
        CountDownListener.super.onCountDownEnd(evt);
        CountDown countDown = (CountDown) evt.getSource();
        if (countDown.getKey().equals("lqs0_countdown")) {
            //TODO: 完成订单支付超时的业务处理逻辑
            this.getView().showMessage("时间已到！");
        }
    }
}
