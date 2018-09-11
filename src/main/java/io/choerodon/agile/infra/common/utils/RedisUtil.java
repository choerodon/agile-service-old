package io.choerodon.agile.infra.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/22
 */
@Component
public class RedisUtil {

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
     * 删除redis缓存
     *
     * @param keys keys
     */
    public void deleteRedisCache(String[] keys) {
        try {
            for (String key : keys) {
                Set<String> caches = keys(key);
                LOGGER.debug("查询到缓存{}，匹配值{}", key, caches.size());
                if (!caches.isEmpty()) {
                    deleteByKey(caches);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("缓存删除失败，失败原因:{}", e);
        }

    }
}
