package cn.tradewar.dao.auth.filter;

import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.core.common.ServiceError;
import cn.tradewar.core.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import cn.tradewar.dao.model.entity.WstUserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * @author wenwst@163.com
 * @date: 20240918
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter{

    @Value("${token.expirationMilliSeconds}")
    private long expirationMilliSeconds;

    private final RedisUtil redisUtil;

    public JwtAuthenticationTokenFilter(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @Nullable FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        response.setCharacterEncoding("utf-8");
        if(null == filterChain) {
            response.getWriter()
                    .write(JSON.toJSONString(
                            GenericResponse.response(ServiceError.UN_KNOW_ERROR)));
            return;
        }
        if (null == authHeader || !authHeader.startsWith("Bearer ")){
            //token格式不正确
            filterChain.doFilter(request,response);
            return;
        }
        String authToken = authHeader.substring("Bearer ".length());

        // 用户的ID userId
        // String userId = JwtTokenUtil.parseToken(authToken);
        //获取redis中的token信息
        if (!redisUtil.hasKey(authToken)){
            //token 不存在 返回错误信息
            response.getWriter()
                    .write(JSON.toJSONString(GenericResponse.response(ServiceError.GLOBAL_ERR_NO_SIGN_IN)));
            return;
        }

        //获取Redis
        Object hgetObject = redisUtil.hget(authToken);

        if (!(hgetObject instanceof HashMap)) {
            response.getWriter()
                    .write(JSON.toJSONString(GenericResponse.response(ServiceError.GLOBAL_ERR_NO_SIGN_IN)));
            return;
        }
        @SuppressWarnings("unchecked")
        HashMap<String, Object> hashMap = (HashMap<String, Object>) hgetObject;
        //从tokenInfo中取出用户信息
        WstUserEntity user = new WstUserEntity();

        Long userId = Long.parseLong(hashMap.get("id").toString());
        @SuppressWarnings("unchecked")
        Set<? extends GrantedAuthority> authorities = (Set<? extends GrantedAuthority>) hashMap.get("authorities");
        user.setId(userId).setAuthorities(authorities);
        //更新token过期时间
        redisUtil.setKeyExpire(authToken,expirationMilliSeconds);
        //将信息交给security
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);
    }
}
