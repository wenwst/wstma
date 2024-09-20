package cn.tradewar.dao.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "w_user")
@TableName("w_user")
public class WstUserEntity implements UserDetails, Serializable{
    /**
     * 用户ID (自增主键)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 性别 (0: 未知, 1: 男, 2: 女)
     */
    @Column(name = "gender")
    private Byte gender;

    /**
     * 生日
     */
    @Column(name = "birthday")
    private LocalDate birthday;

    /**
     * 用户级别 (0: 普通用户, 1: VIP, 2: 管理员)
     */
    @Column(name = "user_level")
    private Byte userLevel;

    /**
     * 昵称
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * 手机号码
     */
    @Column(name = "mobile")
    private String mobile;

    /**
     * 头像 URL
     */
    @Column(name = "avatar")
    private String avatar;

    /**
     * 微信 OpenID
     */
    @Column(name = "weixin_openid")
    private String weixinOpenid;

    /**
     * 分享用户 ID
     */
    @Column(name = "share_user_id")
    private Integer shareUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 创建者 ID
     */
    @Column(name = "create_user_id")
    private Integer createUserId;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 更新者 ID
     */
    @Column(name = "update_user_id")
    private Integer updateUserId;

    /**
     * 上次登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 上次登录 IP 地址
     */
    @Column(name = "last_login_ip")
    private String lastLoginIp;

    /**
     * 用户状态 (0: 正常, 1: 删除)
     */
    @Column(name = "status")
    private Byte status;

    @Transient
    @TableField(exist = false)
    private Set<? extends GrantedAuthority> authorities; // Transient as this is managed by Spring Security

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement according to your needs
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement according to your needs
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement according to your needs
    }

    @Override
    public boolean isEnabled() {
        return status == 0; // Implement according to your needs
    }

    // Setters for custom methods
    public WstUserEntity setAuthorities(Set<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public WstUserEntity setId(Long id) {
        this.id = id;
        return this;
    }
}