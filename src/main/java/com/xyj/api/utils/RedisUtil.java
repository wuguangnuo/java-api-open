package com.xyj.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    /**
     * 一周有多少秒
     */
    private static final long WEEK_SECONDS = 7 * 24 * 60 * 60;

    @Autowired
    public RedisUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 将 key，value 存放到redis数据库中，默认设置过期时间为一周
     *
     * @param key
     * @param value
     */
    public void set(String key, String prefix, Object value) {
        redisTemplate.opsForValue().set(mergeKey(key, prefix), JSONObject.toJSONString(value), WEEK_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 将 key，value 存放到redis数据库中，设置过期时间单位是秒
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void set(String key, String prefix, Object value, long expireTime) {
        redisTemplate.opsForValue().set(mergeKey(key, prefix), JSONObject.toJSONString(value), expireTime, TimeUnit.SECONDS);
    }

    /**
     * 将 key，value 存放到redis数据库中，设置过期时间单位是秒
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void set(String key, String prefix, String value, long expireTime) {
        redisTemplate.opsForValue().set(mergeKey(key, prefix), value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 判断 key 是否在 redis 数据库中
     *
     * @param key
     * @return
     */
    public boolean exists(final String key, String prefix) {
        return redisTemplate.hasKey(mergeKey(key, prefix));
    }

    /**
     * 获取与 key 对应的对象
     *
     * @param key
     * @param clazz 目标对象类型
     * @param <T>
     * @return
     */
    public <T> T get(String key, String prefix, Class<T> clazz) {
        String s = get(key, prefix);
        if (s == null) {
            return null;
        }
        return JSONObject.parseObject(s, clazz);
    }

    /**
     * 获取 key 对应的字符串
     *
     * @param key
     * @return
     */
    public String get(String key, String prefix) {
        return redisTemplate.opsForValue().get(mergeKey(key, prefix));
    }

    /**
     * 删除 key 对应的 value
     *
     * @param key
     */
    public void delete(String key, String prefix) {
        redisTemplate.delete(mergeKey(key, prefix));
    }

    /**
     * merge Key
     */
    private String mergeKey(String key, String prefix) {
        if (Strings.isNullOrEmpty(prefix)) return key;
        return prefix + ":" + key;
    }
}
