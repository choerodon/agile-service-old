package io.choerodon.agile.infra.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/22
 */
@Component
public class RedisUtil {

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);


    /**
     * 查询key,支持模糊查询
     *
     * @param key 传过来时key的前后端已经加入了*，或者根据具体处理
     */
    public Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

    /**
     * 重命名key
     */
    public void renameKey(String key, String newKey) {
        redisTemplate.rename(key, newKey);
    }


    /**
     * 字符串添加信息
     *
     * @param key     key
     * @param obj     可以是单个的值，也可以是任意类型的对象
     * @param timeout 过期时间，单位秒
     */
    public void set(String key, Object obj, Long timeout) {
        redisTemplate.opsForValue().set(key, obj, timeout, TimeUnit.SECONDS);
    }

    /**
     * 字符串添加信息
     *
     * @param key key
     * @param obj 可以是单个的值，也可以是任意类型的对象
     */
    public void set(String key, Object obj) {
        valueOperations.set(key, obj);
    }

    /**
     * 字符串获取值
     *
     * @param key key
     */
    public Object get(String key) {
        return valueOperations.get(key);
    }

    /**
     * 删出key
     * 这里跟下边deleteKey（）最底层实现都是一样的，应该可以通用
     *
     * @param key key
     */
    public void delete(String key) {
        valueOperations.getOperations().delete(key);
    }

    /**
     * 删出key
     * 这里跟下边deleteKey（）最底层实现都是一样的，应该可以通用
     *
     * @param keys keys
     */
    public void deleteByKey(Set<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 添加单个
     *
     * @param key    key
     * @param filed  filed
     * @param domain 对象
     */
    public void hset(String key, String filed, Object domain) {
        hashOperations.put(key, filed, domain);
    }


    /**
     * 添加HashMap
     *
     * @param key key
     * @param hm  要存入的hash表
     */
    public void hset(String key, Map<String, Object> hm) {
        hashOperations.putAll(key, hm);
    }

    /**
     * 删除redis缓存
     *
     * @param keys keys
     */
    public void deleteRedisCache(String[] keys) {
        try {
            for (String key : keys) {
                Set<String> caches = keys(key);
                LOGGER.info("查询到缓存{}，匹配值{}", key, caches.size());
                if (!caches.isEmpty()) {
                    deleteByKey(caches);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("缓存删除失败，失败原因:{}", e);
        }

    }

    /**
     * 查询key和field所确定的值
     *
     * @param key   查询的key
     * @param field 查询的field
     * @return HV
     */
    public Object hget(String key, String field) {
        return hashOperations.get(key, field);
    }

    /**
     * 查询该key下所有值
     *
     * @param key 查询的key
     * @return Object
     */
    public Object hget(String key) {
        return hashOperations.entries(key);
    }

    /**
     * 删除key下所有值
     *
     * @param key 查询的key
     */
    public void deleteKey(String key) {
        hashOperations.getOperations().delete(key);
    }

    /**
     * 判断key和field下是否有值
     *
     * @param key   判断的key
     * @param field 判断的field
     */
    public Boolean hasKey(String key, String field) {
        return hashOperations.hasKey(key, field);
    }

    /**
     * 判断key下是否有值
     *
     * @param key 判断的key
     */
    public Boolean hasKey(String key) {
        return hashOperations.getOperations().hasKey(key);
    }

}
