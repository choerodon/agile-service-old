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
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/22
 */
@Aspect
@Component
public class RedisCacheAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheAspect.class);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

    @Autowired
    private RedisUtil redisUtil;


    private static LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 定义拦截规则：拦截Spring管理的后缀为RepositoryImpl的bean中带有@DataLog注解的方法。
     */
    @Pointcut("(execution(* *.*(..)) && @annotation(io.choerodon.agile.infra.common.annotation.RedisCache))")
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
                switch (redisCache.operation()) {
                    case ADD:
                        result = handleAddCache(redisCache, method, pjp);
                        break;
                    case REMOVE:
                        result = handleRemoveCache(redisCache, method, pjp);
                        break;
                    default:
                        result = pjp.proceed();
                        break;
                }
            } else {
                result = pjp.proceed();
            }
        } catch (Throwable e) {
            LOGGER.error("被拦截方法执行错误", e);
        }
        return result;
    }

    private Object handleRemoveCache(RedisCache redisCache, Method method, ProceedingJoinPoint pjp) throws Throwable {
        if (redisCache.removeKeys().length != 0) {
            Object[] args = pjp.getArgs();
            //需要移除的正则key
            String[] keys = redisCache.removeKeys();
            //使用SPEL进行key的解析
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext standardEvaluationContext = getContext(method, args);
            for (String str : keys) {
                str = parseKey(str, args, standardEvaluationContext, parser);
                Set<String> caches = redisUtil.keys(str);
                LOGGER.info("清除redis缓存\nRedis的KEY值:{},缓存匹配数量:{}\n", str, caches.size());
                if (!caches.isEmpty()) {
                    redisUtil.deleteByKey(caches);
                }
            }
        }
        return pjp.proceed();
    }

    private Object handleAddCache(RedisCache redisCache, Method method,ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        StringBuilder sb = new StringBuilder();
        String key;
        if ("".equals(redisCache.key())) {
            sb.append(method.getName());
            for (Object obj : args) {
                sb.append(obj.toString());
            }
            key = sb.toString();
        } else {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext standardEvaluationContext = getContext(method, args);
            key = parseKey(redisCache.key(), args, standardEvaluationContext, parser);
        }
        Object object = redisUtil.get(key);
        if (object != null) {
            return object;
        } else {
            Object result = pjp.proceed();
            //处理时效
            if (redisCache.ttl() == 0) {
                redisUtil.set(sb.toString(), result);
            } else {
                redisUtil.set(sb.toString(), result, redisCache.ttl());
            }
            return result;
        }
    }

    /**
     * 获取缓存的key
     *
     * @param key    key 定义在注解上，支持SPEL表达式
     * @param args   args
     * @param parser parser
     * @return String
     */
    private String parseKey(String key, Object[] args, StandardEvaluationContext context, ExpressionParser parser) {
        //获取#p0这样的表达式
        List<String> pList = descFormat(key);
        //将p0作为参数放入SPEL上下文中
        for (String p : pList) {
            context.setVariable(p.substring(1), args[Integer.valueOf(p.substring(2))]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }

    private StandardEvaluationContext getContext(Method method, Object[] args) {
        //获取被拦截方法参数名列表(使用Spring支持类库)
        String[] paraNameArr = u.getParameterNames(method);
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return context;
    }

    /**
     * 提取出#p[数字]这样的表达式
     *
     * @param desc desc
     * @return List
     */
    private static List<String> descFormat(String desc) {
        List<String> list = new ArrayList<>();
        Matcher matcher = NUMBER_PATTERN.matcher(desc);
        while (matcher.find()) {
            String t = matcher.group(0);
            list.add(t);
        }
        return list;
    }


}
