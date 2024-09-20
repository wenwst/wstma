package cn.tradewar.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通用 API 响应对象
 * 用于封装接口返回的响应数据，包括请求是否成功、状态码、消息和数据。
 * @author wenwst@163.com
 * @date 20240919
 */
@Data
@AllArgsConstructor
public class GenericResponse {

    // 表示请求是否成功
    private boolean success;

    // HTTP 状态码
    private int statusCode;

    // 返回的内容或数据
    private Object content;

    // 返回的消息提示
    private String msg;

    public GenericResponse(boolean success, int code, String msg, Object data) {
        this.success = success;
        this.statusCode = code;
        this.msg = msg;
        this.content = data;
    }

    public static GenericResponse response(ServiceError error) {

        return GenericResponse.response(error, null);
    }

    /**
     * 根据 ServiceError 和数据生成一个通用的响应对象
     *
     * @param error ServiceError 错误类型，不能为 null
     * @param data 返回的数据，可以为 null
     * @return GenericResponse 响应对象，包含成功状态、状态码、消息和数据
     */
    public static GenericResponse response(ServiceError error, Object data) {
        // 如果传入的 error 为 null，使用未知错误（UN_KNOW_ERROR）作为默认值
        if (error == null) {
            error = ServiceError.UN_KNOW_ERROR;
        }

        // 如果错误类型是正常（NORMAL），则表示请求成功，返回 success = true 的响应
        if (error == ServiceError.NORMAL) {
            return GenericResponse.response(true, error.getCode(), error.getMsg(), data);
        }

        // 否则表示请求失败，返回 success = false 的响应
        return GenericResponse.response(false, error.getCode(), error.getMsg(), data);
    }

    public static GenericResponse response(boolean success, int code, String msg, Object data) {
        // 构造并返回一个新的 GenericResponse 实例
        return new GenericResponse(success, code, msg, data);
    }

    /**
     * 返回固定的参数错误响应
     *
     * @return GenericResponse 参数错误的响应对象
     */
    public static GenericResponse badArgument() {
        return response(ServiceError.ARG_ERROR);
    }

    /**
     * 返回未登录的错误响应
     *
     * @return GenericResponse 未登录的响应对象
     */
    public static GenericResponse unLogin() {
        return response(ServiceError.GLOBAL_ERR_NO_SIGN_IN);
    }
}