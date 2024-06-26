package lqs0.dev.open.plugin.operate;

import com.alibaba.dubbo.common.json.JSONObject;
import com.alipay.easysdk.factory.Factory;
import kd.bos.form.MobileFormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.plugin.AbstractMobFormPlugin;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiParam;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.result.CustomApiResult;
import lqs0.dev.open.common.PayStatus;

import javax.servlet.http.HttpServletRequest;

@ApiController(value = "/alipay", desc = "")
public class AliPayNotifyPlugin extends AbstractMobFormPlugin {
    @ApiPostMapping("/notify")
    public CustomApiResult<String> payNotify(@ApiParam("request") HttpServletRequest request) throws Exception { //HttpServletRequest

        System.out.println(request);

        return CustomApiResult.success(PayStatus.PAY_SUCCESS);
    }
}


/*if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));

            }
            String tradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");

            //支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)) {
                // 验签通过
                System.out.println("交易名称:" + params.get("subject"));
                System.out.println("交易状态:" + params.get("trade_status"));
                System.out.println("支付宝交易凭证号:" + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额:" + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id:" + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));
                //更新订单未已支付

                return CustomApiResult.success(PayStatus.PAY_SUCCESS);
            }
        }*/