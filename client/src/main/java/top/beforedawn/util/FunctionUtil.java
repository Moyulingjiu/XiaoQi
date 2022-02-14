package top.beforedawn.util;

import com.alibaba.fastjson.JSONObject;
import top.beforedawn.models.bo.MyUser;

public class FunctionUtil {
    public static MyUser getLuck(Long userId, Long botId) {
        String s = HttpRequest.sendGet("http://localhost:8080/user/luck/" + userId, "botId=" + botId);
        System.out.println("请求结果：" + s);
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONObject data = jsonObject.getJSONObject("data");
        System.out.println("请求结果：" + data);
        System.out.println("请求结果：" + data.getInteger("luck"));
        MyUser user = new MyUser();
        user.setId(userId);
        user.setLuck(data.getInteger("luck"));
        return user;
    }
}
