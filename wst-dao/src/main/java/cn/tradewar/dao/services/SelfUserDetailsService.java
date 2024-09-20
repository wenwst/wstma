package cn.tradewar.dao.services;

import cn.tradewar.dao.model.entity.WstUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class SelfUserDetailsService implements UserDetailsService {

    /**
     * 根据用户名加载用户信息
     * <p>
     * 如果使用表单鉴权，则需要实现该方法，通过用户名获取用户的相关信息（例如密码、权限等）。
     *
     * @param username 用户名
     * @return UserDetails 用户信息
     * @throws UsernameNotFoundException 如果用户不存在，则抛出该异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过用户名查询用户信息
        // 根据自己的业务逻辑从数据库获取用户信息
        WstUserEntity user = getUserByUsername(username); // 假设这是一个方法，从数据库中获取用户

        // 创建权限集合
        Set<SimpleGrantedAuthority> authoritiesSet = new HashSet<>();
        // 模拟从数据库中获取用户权限（需要根据实际业务进行修改）
        authoritiesSet.add(new SimpleGrantedAuthority("test:list"));
        authoritiesSet.add(new SimpleGrantedAuthority("test:add"));
        user.setAuthorities(authoritiesSet); // 设置用户权限

        log.info("用户{}验证通过", username);
        return user; // 返回用户信息
    }

    /**
     * 模拟从数据库获取用户信息的方法
     * <p>
     * 实际应用中，请根据具体的数据库访问代码实现该方法。
     *
     * @param username 用户名
     * @return WstUserEntity 用户实体
     */
    private WstUserEntity getUserByUsername(String username) {
        // 此处替换为实际的数据库查询逻辑
        // 例如：return userMapper.getUser(username);

        // 模拟返回用户实体
        WstUserEntity user = new WstUserEntity();
        user.setId(1L); // 设置用户 ID（示例）
        user.setUsername(username); // 设置用户名（示例）
        user.setPassword("examplePassword"); // 设置密码（示例，实际中不应明文存储）

        return user; // 返回用户信息
    }
}
