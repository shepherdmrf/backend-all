package shu.xai.sys.enums;

public enum LoginWayCodeEnums {
    LOGIN_EMAIL("1","邮箱登录"),
    LOGIN_TELE("2","手机登录"),
    LOGIN_LOGINNO("3","统一认证号登录"),
    ;

    private String code;
    private String msg;

    LoginWayCodeEnums(String code, String msg) {
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
