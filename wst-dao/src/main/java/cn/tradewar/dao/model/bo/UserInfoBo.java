package cn.tradewar.dao.model.bo;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息
 *
 */
@Data
public class UserInfoBo implements Serializable{

    @Serial
    private static final long serialVersionUID = -5813029516433359765L;

    private Long userId;
    private String openId;
    private String nickName;
    private String avatarUrl;
    private String country;
    private String province;
    private String city;
    private String language;
    private Byte gender;
    private String phone;

    private Byte status;//状态
    private String registerDate;//注册日期

}
