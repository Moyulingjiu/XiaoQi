package top.beforedawn.service.util;

public enum ReturnNo {
    OK(0,"成功"),
    INTERNAL_SERVER_ERR(500,"服务器内部错误"),
    RESOURCE_ID_NOT_EXIST(404,"操作的资源id不存在");

    private final int code;
    private final String message;
    ReturnNo(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
