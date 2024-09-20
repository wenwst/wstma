package cn.tradewar.dao.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTokenBo {
    private Long userId;
    private String token;
    private String sessionKey;
    private LocalDateTime expireTime;
    private LocalDateTime updateTime;
}

