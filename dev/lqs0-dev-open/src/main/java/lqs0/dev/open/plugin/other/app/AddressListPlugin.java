package lqs0.dev.open.plugin.other.app;

import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.events.ListRowClickEvent;
import kd.bos.list.plugin.AbstractMobListPlugin;

public class AddressListPlugin extends AbstractMobListPlugin {

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
        String operateKey = ((FormOperate)args.getSource()).getOperateKey();
        if (StringUtils.equals(operateKey,"default_address")){
            myDefaultAddress(args);
        }
    }

    private void myDefaultAddress(BeforeDoOperationEventArgs args) {
        ListSelectedRowCollection selectedRows = args.getListSelectedData().getBillListSelectedRowCollection();
    }
}
