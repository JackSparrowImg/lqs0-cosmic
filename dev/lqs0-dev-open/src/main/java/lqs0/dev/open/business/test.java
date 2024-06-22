package lqs0.dev.open.business;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.result.CustomApiResult;
import lqs0.dev.open.common.PayStatus;
import javax.servlet.http.HttpServletRequest;
/**
 *
 * API接口回调测试
 * 回调地址：http://tspxep.natappfree.cc/ierp/kapi/v2/lqs0/lqs0_admin/test ,使用natApp进行内网穿透
 */

@ApiController(value = "/test1", desc = "")
public class test {
    @ApiPostMapping("/test")
    public CustomApiResult<String> payNotify() throws Exception {
        System.out.println("test");
        return CustomApiResult.fail(PayStatus.PAY_CANCEL,PayStatus.PAY_CANCEL);
    }
}
