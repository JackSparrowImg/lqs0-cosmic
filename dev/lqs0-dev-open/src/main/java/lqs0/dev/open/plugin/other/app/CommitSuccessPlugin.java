package lqs0.dev.open.plugin.other.app;

import kd.bos.dataentity.utils.StringUtils;
import kd.bos.form.FormShowParameter;
import kd.bos.form.MobileFormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.plugin.AbstractMobFormPlugin;
import kd.bos.list.ListFilterParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;

public class CommitSuccessPlugin extends AbstractMobFormPlugin {

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs) {
        super.beforeDoOperation(eventArgs);


        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();

        if (StringUtils.equals("goorderpage",opKey)){
            goOrderPage(eventArgs);
        }
    }

    private void goOrderPage(BeforeDoOperationEventArgs eventArgs) {

        //若想打开该页面，必须传入订单的id
        Object orderId = this.getView().getFormShowParameter().getCustomParam("orderId");
        MobileFormShowParameter showParameter = new MobileFormShowParameter();
        showParameter.setFormId("lqs0_order_detail_page");
        // 设置显示样式为模态窗口
        showParameter.getOpenStyle().setShowType(ShowType.Floating);
        showParameter.setCustomParam("orderId",Long.parseLong(orderId.toString()));

        // 显示表单
        this.getView().showForm(showParameter);
    }
}
