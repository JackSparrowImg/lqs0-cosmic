package lqs0.dev.open.common.test;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.AlipayConfig;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.FileItem;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

import static lqs0.dev.open.utils.HtmlEncodeToUrlUtil.createAliAppPayUrl;

public class AlipayTradeAppPay {

    public static void main(String[] args) throws AlipayApiException {
        String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCpY0sYX5L27IyYTnBFdrCKIqoHusGFwQf6BbJs3XREN3bj9JhwtUPkuJ1zF6+9o4oQC1n660aWN2nK8X7k2VfbTnar3Wu3p1Gn8+fG0SCwsScYrHbMcz0j2tV98oVDmd0SE/jpNJTN3prHvd7AJUGntg5wZdBFSgOSPk2YlCde9NwYwVSsIFC+Ln6e9wQrwO1ciUKJgZ8/MwBdxF2kS5hnR09NZNu2YVPYAKbh7evnbl4nKTxRIFNtX7dI7/xpYPMipDvbBuSvN7uqZsKheiUbROjhLVSDuXoPcBr+J8G5D833qreOuY/E1H73KX5DXuAwk7hzIdLrSjOJkd92ylbnAgMBAAECggEBAJR/v0Mj7uTZJr0T7yuGzWic0TGrkaNgQkz4F5+HWjm/4lQgiFAvKdFn5GkVN/XqXMyKFx16jEx/UhkgNJ4OyQ4zsKglmxzL7DNxCh5sEtu0w1DHSfMdiFlZrVcYeap1RrFlnw9cEKSXZlx8Yb9UKkYMvoXdTISiSZU5V6/PGuEMGlvDBMIUN+7t6xHjLNeNqckTsJT7EMVZ18WZ3aEguD7BV41HSuR4tjYSEkTfSCzleC03T2U0QRyc548ARDv2QGhiRcMdhUa+hmhK5usM5+IYA5TGgebEJRrcrbI3nenac/Uzgmwam8oDPiXXP9BMNgdz0d6vJSGwGU8wH7wR8sECgYEA8mvsbDNYYVRTAlbX8WOFhFRA8wjbnWRG9E0lNR22lwDchW7cGpvlGVO5P9k4sldfFYHWagwbyRP+cCRsFD0tJ4tNqhx6AaYtgyGlqFbc3hvY8k6wZLDqIpvTd79MmqcYh+3HCPap9Gs1vRVosuuB8vi2Jhha5JEABQ1IoBPyMuECgYEAsuAkMbs5UqPyTGgRP6gFFLXK7gXmqhlcZ60A789EBIjkdl4Pc9/huJYORT4Wtum9iRJUP7h6lTVLJDOm5EgnrJqJ649QisbCJlxBFg8NR5KQ4Ti8vSXk+XNT4haw+9El1OXHU+6SaqbBo9dmnP9YwuilAE4ArDxjBZukpHiICscCgYEAjNBLwH4CtGZ64Tz4qmqYv8kBASsABmidHoNBPZ1I1mXVslw21e4AIboUPRY8bLt7q86DcY4fBanrfdBqR0dR10p/jnSEGjkg7q64vxPVr96VIgq7Q4HQPtIs/73LDXpKMYrYtkgxaJxt1qt86ZELJst5Yp97DJF9ZXHV4cY0RYECgYAd4DXrYHvog516iX8oBKYkmWB1dOLb0jSO1GRtqU6Q+1Q9OOSX0/LdY6Kqif49Orj/ZeJQaj1/IlGUkFP1mN+XgpA6mcs4Ao523b25M3ZiV+Z+130ekm+2Aj8maefRh1MsRdyO9i/aRFPfS6DF5BgtJbI5ZM3T9poLW72aAs+mDQKBgQCLm3KhAjO0vmy0sSCZI4b+ZPORnCX14lQm94oK+5/AJRJtLW5g3xKx9+n6vFaEWS1fDqIsy0FygnFwG8KiUeder3PFPnwlqPrlZh3qP0EFcMpkll+Hxs5l/YC9s0HX77kSg45D11/ifmtYkptTaAWA6gGH+vuhuN1GCVdBZbED6g==";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnz8gll3f3vl873mmcTv1UEHpnqLXB5JZdy4vO8hJ3ulNsdfmYcnlByei+ol9VV543meWEmezZo1ujLDEDJAX/nM9y1AZIh9unqo07c/45g0idDfTvTuyqUddFj4B6y1oGA1z1MBjCsH/yynP47n0Dknj2JfG1kZAZPATrTovoMezBeC7kRmpRMWm9SpjeiknCAIdT6Up35g8wB6K+V2fz1SPdK70vjbYJ09EMVPowRaGwS7/GQJKAkBb9mCYOcB/kMrf1aP0MV3SQWoywQ2SdIqo3/EDsJFfGJHbzzm4xSAPn7AVCcqF4uBSELzoKrPNWg7CwGJ5Nah+BPm4atcXrwIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
        alipayConfig.setAppId("9021000137674054");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");

        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo("70501111111S001111119");
        model.setTotalAmount("9.00");
        model.setSubject("大乐透");
        request.setBizModel(model);

        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
        String orderStr = response.getBody();

        String aliAppPayUrl = createAliAppPayUrl(orderStr);

        System.out.println(orderStr);
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }
    }
}
