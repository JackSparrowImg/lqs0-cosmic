package lqs0.dev.open.plugin.other.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import lqs0.dev.open.common.AliPayConstant;

public class MyAlipayConfig {
    /*private static final String appId = "9021000137674054";
    private static final String appPrivateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCpY0sYX5L27IyYTnBFdrCKIqoHusGFwQf6BbJs3XREN3bj9JhwtUPkuJ1zF6+9o4oQC1n660aWN2nK8X7k2VfbTnar3Wu3p1Gn8+fG0SCwsScYrHbMcz0j2tV98oVDmd0SE/jpNJTN3prHvd7AJUGntg5wZdBFSgOSPk2YlCde9NwYwVSsIFC+Ln6e9wQrwO1ciUKJgZ8/MwBdxF2kS5hnR09NZNu2YVPYAKbh7evnbl4nKTxRIFNtX7dI7/xpYPMipDvbBuSvN7uqZsKheiUbROjhLVSDuXoPcBr+J8G5D833qreOuY/E1H73KX5DXuAwk7hzIdLrSjOJkd92ylbnAgMBAAECggEBAJR/v0Mj7uTZJr0T7yuGzWic0TGrkaNgQkz4F5+HWjm/4lQgiFAvKdFn5GkVN/XqXMyKFx16jEx/UhkgNJ4OyQ4zsKglmxzL7DNxCh5sEtu0w1DHSfMdiFlZrVcYeap1RrFlnw9cEKSXZlx8Yb9UKkYMvoXdTISiSZU5V6/PGuEMGlvDBMIUN+7t6xHjLNeNqckTsJT7EMVZ18WZ3aEguD7BV41HSuR4tjYSEkTfSCzleC03T2U0QRyc548ARDv2QGhiRcMdhUa+hmhK5usM5+IYA5TGgebEJRrcrbI3nenac/Uzgmwam8oDPiXXP9BMNgdz0d6vJSGwGU8wH7wR8sECgYEA8mvsbDNYYVRTAlbX8WOFhFRA8wjbnWRG9E0lNR22lwDchW7cGpvlGVO5P9k4sldfFYHWagwbyRP+cCRsFD0tJ4tNqhx6AaYtgyGlqFbc3hvY8k6wZLDqIpvTd79MmqcYh+3HCPap9Gs1vRVosuuB8vi2Jhha5JEABQ1IoBPyMuECgYEAsuAkMbs5UqPyTGgRP6gFFLXK7gXmqhlcZ60A789EBIjkdl4Pc9/huJYORT4Wtum9iRJUP7h6lTVLJDOm5EgnrJqJ649QisbCJlxBFg8NR5KQ4Ti8vSXk+XNT4haw+9El1OXHU+6SaqbBo9dmnP9YwuilAE4ArDxjBZukpHiICscCgYEAjNBLwH4CtGZ64Tz4qmqYv8kBASsABmidHoNBPZ1I1mXVslw21e4AIboUPRY8bLt7q86DcY4fBanrfdBqR0dR10p/jnSEGjkg7q64vxPVr96VIgq7Q4HQPtIs/73LDXpKMYrYtkgxaJxt1qt86ZELJst5Yp97DJF9ZXHV4cY0RYECgYAd4DXrYHvog516iX8oBKYkmWB1dOLb0jSO1GRtqU6Q+1Q9OOSX0/LdY6Kqif49Orj/ZeJQaj1/IlGUkFP1mN+XgpA6mcs4Ao523b25M3ZiV+Z+130ekm+2Aj8maefRh1MsRdyO9i/aRFPfS6DF5BgtJbI5ZM3T9poLW72aAs+mDQKBgQCLm3KhAjO0vmy0sSCZI4b+ZPORnCX14lQm94oK+5/AJRJtLW5g3xKx9+n6vFaEWS1fDqIsy0FygnFwG8KiUeder3PFPnwlqPrlZh3qP0EFcMpkll+Hxs5l/YC9s0HX77kSg45D11/ifmtYkptTaAWA6gGH+vuhuN1GCVdBZbED6g==";
    private static final String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnz8gll3f3vl873mmcTv1UEHpnqLXB5JZdy4vO8hJ3ulNsdfmYcnlByei+ol9VV543meWEmezZo1ujLDEDJAX/nM9y1AZIh9unqo07c/45g0idDfTvTuyqUddFj4B6y1oGA1z1MBjCsH/yynP47n0Dknj2JfG1kZAZPATrTovoMezBeC7kRmpRMWm9SpjeiknCAIdT6Up35g8wB6K+V2fz1SPdK70vjbYJ09EMVPowRaGwS7/GQJKAkBb9mCYOcB/kMrf1aP0MV3SQWoywQ2SdIqo3/EDsJFfGJHbzzm4xSAPn7AVCcqF4uBSELzoKrPNWg7CwGJ5Nah+BPm4atcXrwIDAQAB";
    private static final String notifyUrl = "http://tspxep.natappfree.cc/ierp/kapi/v2/lqs0/lqs0_admin/notify";*/

    private AlipayConfig alipayConfig = null;


    public AlipayConfig init() throws AlipayApiException {
        //全局仅执行一次
        if(alipayConfig == null){
            alipayConfig =  new AlipayConfig();
            alipayConfig.setServerUrl(AliPayConstant.ALI_SERVER_URL);
            alipayConfig.setCharset("UTF-8");
            alipayConfig.setSignType(AliPayConstant.ALI_SIGN_TYPE);
            alipayConfig.setAlipayPublicKey(AliPayConstant.ALI_PAY_PUBLIC_KEY);
            alipayConfig.setPrivateKey(AliPayConstant.APP_PRIVATE_KEY);
            alipayConfig.setAppId(AliPayConstant.APPID);
            alipayConfig.setFormat("json");

            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

            System.out.println("====================支付宝SDK初始化成功================");
        }
        return alipayConfig;
    }
}
