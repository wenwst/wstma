package cn.tradewar.dao.model.bo;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * WxBindMobileBo 微信绑定手机号码所需的数据。
 *
 * @author wenwst@163.com
 * @date 2024-09-18
 */
@Data
@Builder
public class WxBindMobileBo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7722430332896313642L;

    /**
     * 微信登录后返回的用户 ID
     */
    private Long userId;

    /**
     * mobile 手机号码
     */
    @NotBlank(message = "手机号码不能为空或为空")
    private String mobile;
}
