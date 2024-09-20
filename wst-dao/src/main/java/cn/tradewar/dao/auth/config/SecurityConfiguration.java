package cn.tradewar.dao.auth.config;

import cn.tradewar.dao.auth.handle.WstAccessDeniedHandler;
import cn.tradewar.dao.auth.handle.WstAuthenticationEntryPoint;
import cn.tradewar.dao.auth.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * @author: wenwst@163.com
 * @describe: Security配置
 */
@Configuration
@EnableMethodSecurity()
@EnableWebSecurity
public class SecurityConfiguration {

    private final WstAuthenticationEntryPoint authenticationEntryPoint;
    private final WstAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    public SecurityConfiguration(WstAuthenticationEntryPoint authenticationEntryPoint,
                                 WstAccessDeniedHandler wstAccessDeniedHandler,
                                 JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = wstAccessDeniedHandler;
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // 微信小程序：授权页面
                .authorizeHttpRequests((auth) ->
                        auth.requestMatchers( "/wx/home/",
                                        "/wx/auth/wxLogin",
                                        "/wx/auth/wxLoginPhone").permitAll()
                            .requestMatchers("/wx/user/", "/wx/auth/wxBindMobile").authenticated())
                .sessionManagement(sessionAuthenticationStrategy -> sessionAuthenticationStrategy
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling((exp)-> exp
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .httpBasic(hbc -> hbc.authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults(){
        return new GrantedAuthorityDefaults("");//remove the ROLE_ prefix
    }

}