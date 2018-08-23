package io.choerodon.agile.infra.common.aspect;

import io.choerodon.agile.infra.common.annotation.RedisCache;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 定义拦截规则：拦截Spring管理的后缀为RepositoryImpl的bean中带有@DataLog注解的方法。
     */
    @Pointcut("@annotation(io.choerodon.agile.infra.common.annotation.RedisCache)")
    public void redisCacheMethodPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("redisCacheMethodPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        Object result = null;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RedisCache redisCache = method.getAnnotation(RedisCache.class);
        try {
            if (redisCache != null) {
                EvaluationContext context = new StandardEvaluationContext();
                String[] params = discoverer.getParameterNames(method);
                Object[] args = pjp.getArgs();
                for (int len = 0; len < params.length; len++) {
                    context.setVariable(params[len], args[len]);
                }
                ExpressionParser parser = new SpelExpressionParser();
                Expression expression = parser.parseExpression(redisCache.projectId());
                Long projectId = expression.getValue(context, Long.class);
                if (projectId != 0) {
                    switch (redisCache.operation()) {
                        case ADD:
                            result = handleAddCache(redisCache, method, result, args, projectId, pjp);
                            break;
                        case REMOVE:
                            result = handleRemoveCache(redisCache, result, pjp, projectId);
                            break;
                        default:
                            result = pjp.proceed();
                            break;
                    }
                }
            } else {
                result = pjp.proceed();
            }
        } catch (Throwable e) {
            LOGGER.error("被拦截方法执行错误", e);
        }
        return result;
    }

    private Object handleRemoveCache(RedisCache redisCache, Object result, ProceedingJoinPoint pjp, Long projectId) throws Throwable {
        if (redisCache.cacheNames().length != 0) {
            for (String str : redisCache.cacheNames()) {
                LOGGER.info("**********从Redis中删除数据**********\nRedis的KEY值:{}", str + projectId);
                String pattern = str + "(" + projectId + ")" + "*";
                Set<String> caches = redisUtil.keys(pattern);
                if (!caches.isEmpty()) {
                    redisUtil.deleteByKey(caches);
                }
            }
        }
        result = pjp.proceed();
        return result;
    }

    private Object handleAddCache(RedisCache redisCache, Method method, Object result, Object[] args, Long projectId, ProceedingJoinPoint pjp) throws Throwable {
        StringBuilder sb = new StringBuilder();
        if ("".equals(redisCache.key())) {
            sb.append(method.getName());
        } else {
            sb.append(redisCache.key()).append("(").append(projectId).append(")");
        }
        for (Object obj : args) {
            sb.append(obj.toString());
        }
        String key = sb.toString();
        Object object = redisUtil.get(key);
        if (object != null) {
            LOGGER.info("**********从Redis中查到了数据**********\nRedis的KEY值:{}\nRedis的VALUE值{}", sb.toString(), object.toString());
            return object;
        } else {
            result = pjp.proceed();
            LOGGER.info("**********没有从Redis中查到数据**********");
            //处理时效
            if (redisCache.ttl() == 0) {
                redisUtil.set(sb.toString(), result);
            } else {
                redisUtil.set(sb.toString(), result, redisCache.ttl());
            }
            return result;
        }
    }


}
