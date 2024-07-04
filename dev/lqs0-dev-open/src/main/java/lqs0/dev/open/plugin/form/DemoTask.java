package lqs0.dev.open.plugin.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.HashMap;
import java.util.Map;

public class DemoTask implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String , String> result = new HashMap<>();
        if ("GET_CANTEEN_DISHES".equalsIgnoreCase(action)) {
           /* //从上一个节点中获取食堂canteen数据
            String canteen = params.get("canteen");
            //获取DynamicObject列表
            DynamicObject[] dys = BusinessDataServiceHelper.load("lqs0_dish_evaluate",
                    "number," +
                            "lqs0_basedatapropfield1," +
                            "name," +
                            "lqs0_score," +
                            "lqs0_content",
                    (new QFilter("lqs0_basedatapropfield1", QCP.equals, canteen)).toArray());*/
            //创建一个JsonArray
            JSONArray jsonArray = new JSONArray();
            //for (DynamicObject dynamicObject : dys) {
                //将每一个评价信息加入JSONArray
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("number", "test");
                jsonObject.put("canteen", "test");
                jsonObject.put("name", "test");
                jsonObject.put("score", "test");
                jsonObject.put("content", "test");
                jsonArray.add(jsonObject);
            //}
            System.out.println(jsonArray.toJSONString());
            //加入resultDynamicObject参数，将JsonArray加入到这个参数当中，然后返回
            result.put("resultDynamicObject", jsonArray.toJSONString());
        }
        return result;
    }
}
