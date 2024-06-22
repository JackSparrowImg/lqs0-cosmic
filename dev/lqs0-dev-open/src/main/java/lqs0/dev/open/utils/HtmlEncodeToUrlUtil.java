package lqs0.dev.open.utils;

import lqs0.dev.open.common.AliPayConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlEncodeToUrlUtil {

    public static String createAliAppPayUrl(String beforeUrl){

        StringBuilder result = new StringBuilder();
        result.append(AliPayConstant.ALI_SERVER_URL+ "?") ;

        String a = beforeUrl.substring(0, beforeUrl.indexOf("&biz_content="));

        String bizContentKey = "&biz_content=";

        String c = beforeUrl.substring(beforeUrl.indexOf(bizContentKey) + bizContentKey.length(), beforeUrl.indexOf("&charset="));

        c = bizContentKey + c; // 添加key以保持格式

        // 截取剩余部分
        String b = beforeUrl.substring(beforeUrl.indexOf("&charset="));

        result.append(a + b + c);

        return result.toString();
    }



    public static String extractActionUrl(String htmlForm) {
        Pattern pattern = Pattern.compile("action=\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(htmlForm);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String extractBizContent(String htmlForm) {
        Pattern pattern = Pattern.compile("name=\"biz_content\" value=\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(htmlForm);
        if (matcher.find()) {
            return matcher.group(1).replace("&quot;", "\"");
        }
        return null;
    }

    public static Map<String, String> splitQuery(String url) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new HashMap<>();
        int queryStart = url.indexOf('?');
        if (queryStart == -1) {
            return queryPairs;
        }
        String query = url.substring(queryStart + 1);
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(
                    URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
            );
        }
        return queryPairs;
    }
}
