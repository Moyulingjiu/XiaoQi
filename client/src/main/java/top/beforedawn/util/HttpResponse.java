package top.beforedawn.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求信息返回通用类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {
    private int code;
    private String message;
    private JSONObject data;

    public HttpResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpResponse(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        code = jsonObject.getInteger("code");
        message = jsonObject.getString("message");
        try {
            data = jsonObject.getJSONObject("data");
        } catch (JSONException exception) {
            // 如果不是一个类的话，那就是一个元数据
            data = jsonObject;
        }
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
