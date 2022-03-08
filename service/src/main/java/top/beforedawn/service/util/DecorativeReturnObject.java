package top.beforedawn.service.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 装饰的统一返回类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecorativeReturnObject {
    private int code;
    private String message;
    private Object data;

    public DecorativeReturnObject(ReturnObject ret) {
        code = ret.getCode().getCode();
        message = ret.getCode().getMessage();
        data = ret.getData();
    }
}
