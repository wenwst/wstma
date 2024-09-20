package cn.tradewar.dao.auth.utils;

import cn.tradewar.dao.model.entity.WstUserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @description JWT 生成和解析工具类
 */
public class JwtTokenUtil {
    // 加密解密盐值
    private static final String SALT = "wstmaSalt";

    /**
     * 生成 JWT token
     *
     * @param subject           主题信息（例如用户ID）
     * @param expirationSeconds 过期时间（秒）
     * @param claims            自定义身份信息
     * @return 生成的 JWT token 字符串
     */
    @SuppressWarnings("unused")
    public static String generateToken(String subject, int expirationSeconds, Map<String, Object> claims) {
        // 设置 token 的过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + expirationSeconds * 1000L);

        return Jwts.builder()
                .setClaims(claims)                      // 设置自定义属性
                .setSubject(subject)                    // 设置主题
                .setExpiration(expirationDate)          // 设置过期时间
                .signWith(SignatureAlgorithm.HS512, SALT) // 使用 HS512 算法签名
                .compact();                             // 生成紧凑的 JWT
    }

    /**
     * 生成 JWT token
     *
     * @param user           用户实体，包含用户信息
     * @param expirationDate 过期时间
     * @return 生成的 JWT token 字符串
     */
    public static String generateToken(WstUserEntity user, Date expirationDate) {
        // 将 SALT 解码为字节数组
        String base64Salt = Base64.getEncoder().encodeToString(SALT.getBytes());
        byte[] secretKey = Base64.getDecoder().decode(base64Salt);

        // 构建并返回 JWT token
        return Jwts.builder()
                .setSubject(user.getId().toString())   // 设置主题为用户 ID
                .setExpiration(expirationDate)           // 设置过期时间
                .setIssuedAt(new Date())                 // 设置签发时间
                .setIssuer("JAMES")                      // 设置签发者
                .signWith(SignatureAlgorithm.HS512, secretKey) // 使用 HS512 算法签名
                .compact();                              // 生成紧凑的 JWT
    }

    /**
     * 解析 JWT token 并获得主题信息
     *
     * @param token 要解析的 JWT token
     * @return 主题信息（例如用户 ID），如果解析失败则返回 null
     */
    @SuppressWarnings("unused")
    public static String parseToken(String token) {
        try {
            return getTokenBody(token).getSubject(); // 从 token 中获取主题
        } catch (Exception e) {
            // 解析失败时可进行日志记录或处理
            return null; // 返回 null 表示解析失败
        }
    }

    /**
     * 获取 JWT token 中的自定义属性
     *
     * @param token 要解析的 JWT token
     * @return token 中的自定义属性，解析失败时返回 null
     */
    @SuppressWarnings("unused")
    public static Map<String, Object> getClaims(String token) {
        try {
            return getTokenBody(token); // 获取 token 的负载信息
        } catch (Exception e) {
            // 解析失败时可进行日志记录或处理
            return null; // 返回 null 表示解析失败
        }
    }

    /**
     * 解析 JWT token 并获取其负载信息
     *
     * @param token 要解析的 JWT token
     * @return token 的负载信息
     */
    private static Claims getTokenBody(String token) {
        return Jwts.parser()
                .setSigningKey(SALT) // 设置签名密钥
                .parseClaimsJws(token) // 解析 token
                .getBody();           // 获取负载信息
    }
}
