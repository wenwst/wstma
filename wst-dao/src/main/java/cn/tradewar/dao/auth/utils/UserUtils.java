package cn.tradewar.dao.auth.utils;

import cn.tradewar.dao.model.entity.WstUserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户工具类，用于从 SecurityContext 中获取当前登录用户的信息
 */
public  class UserUtils {

    /**
     * 获取当前登录用户的 userId
     * @return 用户ID
     */
    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof WstUserEntity currentUser) {
            return currentUser.getId();  // 获取 userId
        }

        throw new IllegalStateException("当前用户未登录，无法获取用户ID");
    }
}
