package top.beforedawn.util;

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
        data = jsonObject.getJSONObject("data");
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
