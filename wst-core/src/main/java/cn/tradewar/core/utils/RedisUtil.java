package cn.tradewar.core.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类，用于操作Redis缓存
 */
@Component
public class RedisUtil {

    // 从配置文件中读取Token的过期时间（以毫秒为单位）
    @Value("${token.expirationMilliSeconds}")
    private long expirationMilliSeconds;

    private final  RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

    }

    /**
     * 查询key，支持模糊查询
     * @param key 要查询的键
     * @return 符合条件的key的集合
     */
    public Set<String> keys(String key){
        return redisTemplate.keys(key);
    }

    /**
     * 获取字符串类型的值
     * @param key 要查询的键
     * @return 对应的值
     */
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 存储字符串类型的值，使用默认的过期时间（2小时）
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value){
        set(key, value, expirationMilliSeconds);
    }

    /**
     * 存储字符串类型的值，并设置自定义的过期时间
     * @param key 键
     * @param value 值
     * @param expire 过期时间（毫秒）
     */
    public void set(String key, String value, long expire){
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.MILLISECONDS);
    }

    /**
     * 删除指定的key
     * @param key 要删除的键
     */
    public void delete(String key){
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    /**
     * 向哈希表中添加单个值
     * @param key 键
     * @param filed 哈希字段
     * @param domain 要存储的对象
     */
    public void hset(String key, String filed, Object domain){
        hset(key, filed, domain, expirationMilliSeconds);
    }

    /**
     * 向哈希表中添加单个值，并设置过期时间
     * @param key 键
     * @param filed 哈希字段
     * @param domain 要存储的对象
     * @param expire 过期时间（毫秒）
     */
    public void hset(String key, String filed, Object domain, long expire){
        redisTemplate.opsForHash().put(key, filed, domain);
        setKeyExpire(key, expirationMilliSeconds);
    }

    /**
     * 向哈希表中批量添加数据
     * @param key 键
     * @param hm 要存储的哈希表
     */
    public void hset(String key, HashMap<String, Object> hm){
        redisTemplate.opsForHash().putAll(key, hm);
        setKeyExpire(key, expirationMilliSeconds);
    }

    /**
     * 如果哈希字段不存在，则添加值
     * @param key 键
     * @param filed 哈希字段
     * @param domain 要存储的对象
     */
    public void hsetAbsent(String key, String filed, Object domain){
        redisTemplate.opsForHash().putIfAbsent(key, filed, domain);
    }

    /**
     * 查询指定key和field的值
     * @param key 键
     * @param field 哈希字段
     * @return 对应的值
     */
    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 查询指定key下所有哈希表数据
     * @param key 键
     * @return 哈希表的所有键值对
     */
    public Object hget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除指定key下所有的哈希表数据
     * @param key 要删除的键
     */
    public void deleteKey(String key) {
        redisTemplate.opsForHash().getOperations().delete(key);
    }

    /**
     * 向Set集合中添加数据，并设置过期时间
     * @param key 键
     * @param set 要存储的Set集合
     * @param expire 过期时间（毫秒）
     */
    public void sset(String key, Set<?> set, long expire){
        redisTemplate.opsForSet().add(key, set);
        setKeyExpire(key, expire);
    }

    /**
     * 向Set集合中添加数据，使用默认的过期时间
     * @param key 键
     * @param set 要存储的Set集合
     */
    public void sset(String key, Set<?> set){
        sset(key, set, expirationMilliSeconds);
    }

    /**
     * 判断指定的key和field是否存在
     * @param key 键
     * @param field 哈希字段
     * @return 是否存在
     */
    public Boolean hasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 判断指定的key是否存在
     * @param key 键
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.opsForHash().getOperations().hasKey(key);
    }

    /**
     * 设置指定key的过期时间
     * @param key 键
     * @param expire 过期时间（毫秒）
     */
    public void setKeyExpire(String key, long expire){
        redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
    }

}
