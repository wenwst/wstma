package cn.tradewar.core.common;

import lombok.Getter;

/**
 * @author wenwst@163.com
 * @date 20240919
 */
@Getter
public enum ServiceError {

    // 成功和常规错误
    NORMAL(1, "操作成功"),
    UN_KNOW_ERROR(-1, "未知错误"),
    ARG_ERROR(-1, "参数错误"),

    /** 全局错误 */
    GLOBAL_ERR_NO_SIGN_IN(-10001, "未登录或登录过期/Not signed in"),
    GLOBAL_ERR_NO_CODE(-10002, "code 错误/Error code"),
    GLOBAL_ERR_NO_AUTHORITY(-10003, "没有操作权限/No operating rights"),

    /** 微信小程序相关错误 */
    WX_CODE_ERR(-20001, "获取微信 openId 出错"),
    WX_MOBILE_ERROR(-20002, "获取微信手机号码出错"),
    WX_MOBILE_DIFF(-20003, "手机号码不匹配"),
    WX_MOBILE_UPDATE_ERR(-20004, "手机号码不匹配"),

    ;

    // 错误码
    private final int code;

    // 错误信息
    private final String msg;

    /**
     * 构造函数，初始化错误码和错误信息
     * @param code 错误码
     * @param msg 错误信息
     */
    ServiceError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
