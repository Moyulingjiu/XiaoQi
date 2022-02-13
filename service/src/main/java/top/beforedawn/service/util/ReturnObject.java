package top.beforedawn.service.util;

import lombok.Data;

/**
 * 统一返回类
 *
 * @author 墨羽翎玖
 */
@Data
public class ReturnObject<T> {
    private ReturnNo code;
    private T data;

    public ReturnObject() {
        code = ReturnNo.OK;
        data = null;
    }

    public ReturnObject(ReturnNo returnNo) {
        code = returnNo;
    }

    public ReturnObject(T data) {
        code = ReturnNo.OK;
        this.data = data;
    }

    public ReturnObject(ReturnNo returnNo, T data) {
        code = returnNo;
        this.data = data;
    }
}
