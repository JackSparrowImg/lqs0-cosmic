package lqs0.dev.open.plugin.other.app;

import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.control.Control;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.form.plugin.AbstractMobFormPlugin;

public class AddressFormPlugin extends AbstractMobFormPlugin {
    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);

        String opKey = ((FormOperate)args.getSource()).getOperateKey();

        if (StringUtils.equals("save",opKey)){
            saveCheck(args);
        }
    }

    private void saveCheck(BeforeDoOperationEventArgs args) {
        FormOperate formOperate = (FormOperate) args.getSource();
        Control control = formOperate.getView().getControl("lqs0_address");
        String string = control.getView().getModel().getDataEntity().get("name").toString(); //获取到地址内容
        Control nameControl = formOperate.getView().getControl("name");
        nameControl.getView().getModel().getDataEntity().set("name",nameControl);
    }

}
