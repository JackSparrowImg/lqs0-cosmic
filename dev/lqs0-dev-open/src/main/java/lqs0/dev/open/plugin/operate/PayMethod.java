package lqs0.dev.open.plugin.operate;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;

import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import lqs0.dev.open.common.AliPayConstant;
import lqs0.dev.open.plugin.other.config.MyAlipayConfig;

public class PayMethod {

    public String pay(String value, Object allmoney, Object orderId, Object shopName) {
        switch (value){
            case "1":
                String result1 = WeiXinPay(allmoney,orderId,shopName);
                if (result1  != null) {
                    return result1;
                } else {
                    return result1;
                }
            case "2":
                String result2 = ALiPay(allmoney,orderId,shopName);
                if (result2 != null) {
                    return result2;
                } else {
                    return result2;
                }
            case "3":
                String result3 = SchoolCardPay(allmoney,orderId,shopName);
                if (result3 != null) {
                    return result3;
                } else {
                    return result3;
                }
            default: return "";
        }
    }

    private String SchoolCardPay(Object allmoney,Object orderId,Object shopName) {
        //TODO：校园卡支付实现
        return "测试";
    }

    private String ALiPay(Object allmoney, Object orderId, Object shopName) {

        //TODO：支付宝支付实现，手机网页支付
        MyAlipayConfig myAlipayConfig = new MyAlipayConfig();
        AlipayClient alipayClient = null;
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayResponse response = null;


        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(orderId.toString());
        model.setTotalAmount(allmoney.toString());
        model.setSubject(shopName.toString());
        model.setProductCode(AliPayConstant.PRODUCT_CODE);
        model.setSellerId(AliPayConstant.SELLER_ID);

        request.setBizModel(model);
        request.setNotifyUrl(AliPayConstant.NOTIFY_URL);

        request.setReturnUrl(AliPayConstant.RETURN_URL + Long.parseLong(orderId.toString()));

        try {
            AlipayConfig alipayConfig = myAlipayConfig.init();
            alipayClient =  new DefaultAlipayClient(alipayConfig);
            response = alipayClient.pageExecute(request, "GET");

        } catch (AlipayApiException e) {
            System.out.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(),e);
        }
        return response.getBody();
    }

    private String WeiXinPay(Object allmoney, Object orderId, Object shopName) {
        return "测试";
    }


}
