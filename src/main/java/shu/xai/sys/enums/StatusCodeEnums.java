package shu.xai.sys.enums;

public enum StatusCodeEnums {
    ACTIVE("1","激活"),
    IN_ACTIVE("0","未激活"),
    ;

    private String code;
    private String msg;

    StatusCodeEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
