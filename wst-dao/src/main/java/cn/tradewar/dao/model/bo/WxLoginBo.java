package cn.tradewar.dao.model.bo;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;


/**
 * WxLoginBo 是一个业务对象（BO），用于封装微信登录所需的数据。
 * 该类包含登录凭证以及通过微信 API 提供的加密数据。
 * @author: wenwst@163.com
 * @date: 20240918
 */
@Data
@Builder
public class WxLoginBo implements Serializable{

    /**
     * serialVersionUID 用于确保在序列化和反序列化时，不同版本的类之间保持兼容性。
     */
    @Serial
    private static final long serialVersionUID = -7722430332896313642L;

    /**
     * 微信登录后返回的授权码，用于交换会话信息（如 openId）。
     */
    @NotBlank(message = "Code 不能为空或空白")
    private String code;

    /**
     * 微信提供的加密用户数据，通常包含敏感信息，如用户的 unionId 或手机号码（视具体场景而定）。
     */
    private String encryptedData;

    /**
     * 用户的手机号码（可能是可选项或通过微信数据获取）。
     * 该字段通常包含在微信的 encryptedData 中（如果有请求手机号码）。
     */
    private String phone;

    /**
     * 用于解密 {@code encryptedData} 的初始化向量（IV），由微信与加密数据一起提供。
     */
    private String iv;
}
