package lqs0.dev.open.plugin.other.app;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Label;
import kd.bos.form.plugin.AbstractMobFormPlugin;
import kd.bos.list.BillList;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.EventObject;
import java.util.Map;

public class OrderDetailPagePlugin extends AbstractMobFormPlugin {
    @Override
    public void beforeBindData(EventObject e) {
        super.beforeBindData(e);

        Object orderId = this.getView().getFormShowParameter().getCustomParam("orderId");

        QFilter qFilter = new QFilter("lqs0_order_number",QCP.equals,orderId);
        BillList billList = this.getControl("lqs0_billlistap");
        billList.setFilter(qFilter);


        //TODO:根据主键值过滤，找到具体的菜品详情列表，和具体的菜品信息
        QFilter idFilter = new QFilter("id", QCP.equals,orderId);
        DynamicObject myOrders = BusinessDataServiceHelper.loadSingle("lqs0_order",
                "number,lqs0_status,lqs0_order_time,lqs0_pay_method,lqs0_address_num," +
                        "lqs0_address_name,lqs0_user_name,lqs0_phone,lqs0_xuan,lqs0_shopname," +
                        "lqs0_amount,lqs0_estimated_time" , new QFilter[]{idFilter});

        Object lqs0Amount = myOrders.get("lqs0_amount");
        Object estimatedTime = myOrders.get("lqs0_estimated_time");
        Object addressName = myOrders.get("lqs0_address_num.lqs0_address.name");
        Object userName = myOrders.get("lqs0_address_num.lqs0_userfield.name");
        Object phone = myOrders.get("lqs0_address_num.lqs0_phone");
        Object number = myOrders.get("number");
        Object orderTime = myOrders.get("lqs0_order_time");
        Object payMethod = myOrders.get("lqs0_pay_method");
        Object shopname = myOrders.get("lqs0_shopname");
        Object orderStatus = myOrders.get("lqs0_status");

        //设置店铺名称标签
        Label lqs0Labelap = this.getView().getControl("lqs0_labelap");

        switch (orderStatus.toString()){
            case "2": lqs0Labelap.setText("等待商户接单");break;
            case "3": lqs0Labelap.setText("等待商户派送");break;
            case "4": lqs0Labelap.setText("订单正在派送中");break;
            case "5": lqs0Labelap.setText("订单已经完成");break;
            case "6": lqs0Labelap.setText("订单已经取消");break;
            case "7": lqs0Labelap.setText("订单已退款完成");break;
            default: lqs0Labelap.setText("订单出现异常，请稍后查看");
        }


        //设置店铺名称标签
        Label shopName = this.getView().getControl("lqs0_shopname");
        shopName.setText(shopname.toString());

        //设置总金额标签
        Label amountLabel = this.getView().getControl("lqs0_allmoney1");
        String  str = String.format("%.2f",lqs0Amount);
        lqs0Amount = Double.parseDouble(str);
        amountLabel.setText("￥" + lqs0Amount.toString());

        //设置订单编号标签
        Label orderNumLabel = this.getView().getControl("lqs0_order_num1");
        orderNumLabel.setText(number.toString());

        //设置订单时间标签
        Label orderTimeLabel = this.getView().getControl("lqs0_order_time1");
        orderTimeLabel.setText(orderTime.toString());

        //设置订单编号标签
        Label payMethodLabel = this.getView().getControl("lqs0_pay_method1");
        switch (payMethod.toString()){
            case "1": payMethodLabel.setText("微信");break;
            case "2": payMethodLabel.setText("支付宝");break;
            case "3": payMethodLabel.setText("校园卡");break;
            default: payMethodLabel.setText("未知");
        }

        //设置餐具数量标签
        Label num1Label = this.getView().getControl("lqs0_num1");
        num1Label.setText("商家按餐量提供");

        //设置收货人标签
        Label phoneNameLabel = this.getView().getControl("lqs0_address1");
        phoneNameLabel.setText(phone.toString() + " " + userName.toString());

        //设置收货地址标签
        Label addressLabel = this.getView().getControl("lqs0_address2");
        addressLabel.setText(addressName.toString());

        //设置期望收货时间标签
        Label expectTimeLabel = this.getView().getControl("lqs0_expect_time1");
        expectTimeLabel.setText(estimatedTime.toString());

    }
}
