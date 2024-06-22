package lqs0.dev.open.plugin.other.app;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.ShowType;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.BillList;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.MobileListShowParameter;
import kd.bos.list.plugin.AbstractMobListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import lqs0.dev.open.common.CanteenConstant;

public class openDish extends AbstractMobListPlugin {
    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs eventArgs) {
        super.beforeDoOperation(eventArgs);

        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段
        System.out.println(listColumns);

        String opKey = ((FormOperate)eventArgs.getSource()).getOperateKey();

        if (StringUtils.equals("opendish",opKey)){
            openDishPage(eventArgs);
        }else if(StringUtils.equals("southern_canteen",opKey) || StringUtils.equals("north_anteen",opKey)||
            StringUtils.equals("western_canteen",opKey) || StringUtils.equals("east_canteen",opKey)
        ){
            openShopPage(opKey);
        }
    }

    private void openShopPage(String opKey) {
        String canteen = null;
        if(StringUtils.equals("southern_canteen",opKey)){
            canteen = CanteenConstant.SOUTHERN_CANTEEN;
        }else if (StringUtils.equals("north_anteen",opKey)){
            canteen = CanteenConstant.NORTH_CANTEEN;
        }else if (StringUtils.equals("western_canteen",opKey)){
            canteen = CanteenConstant.WESTERN_CANTEEN;
        }else if (StringUtils.equals("east_canteen",opKey)){
            canteen = CanteenConstant.EAST_CANTEEN;
        }

        MobileListShowParameter showParameter = new MobileListShowParameter();

        showParameter.setBillFormId("lqs0_shop"); // 这里是列表对应的单据标识
        showParameter.setFormId("bos_moblist"); // 这里是列表的模板标识，可以在设计器列表页寻找

        // 设置显示样式为模态窗口
        showParameter.getOpenStyle().setShowType(ShowType.Floating);

        //根据食堂区域进行筛选
        QFilter qFilter = new QFilter("lqs0_basedatafield.name",QCP.equals,canteen);
        ListFilterParameter filterParameter = new ListFilterParameter();
        filterParameter.setFilter(qFilter);
        showParameter.setListFilterParameter(filterParameter);

        // 显示表单
        this.getView().showForm(showParameter);

    }

    private void openDishPage(BeforeDoOperationEventArgs eventArgs) {

        ListSelectedRowCollection listColumns= eventArgs.getListSelectedData();//获得列表所有的字段
        if (listColumns == null || listColumns.size() == 0){
            BillList billList = this.getControl("lqs0_billlistap");
            listColumns = billList.getSelectedRows();
        }

        //获取当前点击的菜品的主键值
        Object primaryKey = listColumns.get(0).getPrimaryKeyValue();

        MobileListShowParameter showParameter = new MobileListShowParameter();
        showParameter.setCustomParam("shopId",primaryKey);
        showParameter.setBillFormId("lqs0_dish"); // 这里是列表对应的单据标识
        showParameter.setFormId("bos_moblist"); // 这里是列表的模板标识，可以在设计器列表页寻找
        // 设置显示样式为模浮动
        showParameter.getOpenStyle().setShowType(ShowType.Floating);


        QFilter qFilter = new QFilter("lqs0_shopname",QCP.equals,primaryKey);
        ListFilterParameter filterParameter = new ListFilterParameter();
        filterParameter.setFilter(qFilter);
        showParameter.setListFilterParameter(filterParameter);
        // 显示表单
        this.getView().showForm(showParameter);
    }


}
