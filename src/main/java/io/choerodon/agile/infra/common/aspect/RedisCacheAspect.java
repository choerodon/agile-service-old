package io.choerodon.agile.infra.common.aspect;

import io.choerodon.agile.infra.common.annotation.RedisCache;
import io.choerodon.agile.infra.common.enums.RedisOperation;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.core.exception.CommonException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/22
 */
@Aspect
@Component
public class RedisCacheAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheAspect.class);

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 定义拦截规则：拦截Spring管理的后缀为RepositoryImpl的bean中带有@DataLog注解的方法。
     */
    @Pointcut("@annotation(io.choerodon.agile.infra.common.annotation.RedisCache)")
    public void redisCacheMethodPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("redisCacheMethodPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();
        Object result = null;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RedisCache redisCache = method.getAnnotation(RedisCache.class);
        Object[] args = pjp.getArgs();
        if (args != null && redisCache != null && !"0".equals(redisCache.projectId())) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : args) {
                sb.append(obj.toString());
            }
            sb.append(redisCache.projectId());
            if (RedisOperation.ADD.equals(redisCache.operation()) && !"".equals(redisCache.key())) {
                String key = sb.append(redisCache.key()).toString();
                Object object = redisUtil.get(key);
                if (object != null) {
                    LOGGER.info("**********从Redis中查到了数据**********\nRedis的KEY值:{}\nRedis的VALUE值{}", key, object.toString());
                    return object;
                } else {
                    try {
                        result = pjp.proceed();
                    } catch (Throwable e) {
                        LOGGER.error("方法执行错误", e);
                    }
                    LOGGER.info("**********没有从Redis中查到数据**********");
                    redisUtil.set(key, result,redisCache.ttl());
                    long endTime = System.currentTimeMillis();
                    LOGGER.info("Redis缓存AOP处理所用时间:{}", endTime - startTime);
                    return result;
                }
            } else if (RedisOperation.REMOVE.equals(redisCache.operation()) && redisCache.cacheNames().length != 0) {
                LOGGER.info("**********删除Redis数据**********");
                for (String str : redisCache.cacheNames()) {
                    String key = sb.toString() + str;
                    redisUtil.delete(key);
                }
                try {
                    result = pjp.proceed();
                } catch (Throwable e) {
                    LOGGER.error("方法执行错误", e);
                }
                long endTime = System.currentTimeMillis();
                LOGGER.info("Redis缓存AOP处理所用时间:{}", endTime - startTime);
                return result;
            } else {
                throw new CommonException("执行redis拦截器错误");
            }
        } else {
            throw new CommonException("执行redis拦截器错误");
        }
    }

}
