package shu.xai.sys.enums;

public enum ResultCodeEnums {
    UNKNOWN_ERROR(-1,"系统异常，请联系管理员或重试"),
    SESSION_OVERDUR(0,"用户未登录或登录超时,请重新登录"),
    SUCCESS(1,"执行成功"),
    NO_USER(2,"用户不存在或停用中"),
    ERR_PASSWORD(3,"密码错误"),
    ERR_STYLE(4,"账号或密码格式错误"),
    UPDATE_ERROR(5,"未更新成功"),
    USER_EXIST(6,"认证号已被占用"),
    MENU_EXIST(7,"目录号已存在"),
    OP_EXIST(8,"操作url已存在"),
    FILE_NULL(9,"文件不存在"),

    EMAIL_EXIST(11,"邮箱已被占用"),
    TELEPHONE_EXIST(12,"手机号已被占用"),
    LOGINNO_EXIST(13,"统一认证号已被占用"),
    USERNAME_EXIST(14,"用户名已被占用"),
    MENUID_EXIST(15,"菜单编号已被占用"),
    MENUPATH_EXIST(16,"菜单路径已被占用"),
    EMAIL_FAIL(17,"邮件发送失败"),
    REPEAT_ACTIVE(18,"账号已激活，请勿重复操作"),
    REPEAT_FILENAME(19, "文件名重复"),
    FILE_ERROR(20,"文件错误" ),
    VALIDATION_ERROR(21, "文件验证失败，仅支持 .cif 文件"),
    PARTIAL_SUCCESS(22, "部分操作成功"),
    DUPLICATE_FILES(23, "存在重复文件，等待用户确认操作"),
    INVALID_REQUEST(400, "无效的请求参数");

    private Integer code;
    private String msg;

    ResultCodeEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
