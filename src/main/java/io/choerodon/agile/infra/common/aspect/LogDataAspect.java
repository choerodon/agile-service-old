package io.choerodon.agile.infra.common.aspect;

import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.core.exception.CommonException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;

/**
 * 日志切面
 *
 * @author dinghuang123@gmail.com
 * @since 2018/7/23
 */
@Aspect
@Component
public class LogDataAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogDataAspect.class);

    /**
     * 定义拦截规则：拦截Spring管理的后缀为RepositoryImpl的bean中带有@DataLog注解的方法。
     */
    @Pointcut("bean(*RepositoryImpl) && @annotation(io.choerodon.agile.infra.common.annotation.DataLog)")
    public void updateMethodPointcut() {
    }

    @Around("updateMethodPointcut()")
    public Object Interceptor(ProceedingJoinPoint pjp) {
        long beginTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod(); //获取被拦截的方法
        String methodName = method.getName(); //获取被拦截的方法名
        Set<Object> allParams = new LinkedHashSet<>(); //保存所有请求参数，用于输出到日志中
        Object[] args = pjp.getArgs();
        logger.info("开始记录日志：{}", methodName);

        Object result = null;
        for (Object arg : args) {
            if (arg instanceof Map<?, ?>) {
                //提取方法中的MAP参数，用于记录进日志中
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) arg;
                allParams.add(map);
            } else if (arg instanceof IssueE) {


            } else {
                //allParams.add(arg);
            }
        }

        try {
            // 一切正常的情况下，继续执行被拦截的方法
            result = pjp.proceed();
        } catch (Throwable e) {
            logger.info("exception: ", e);
            result = new CommonException("");
        }

        if (result != null) {
            long costMs = System.currentTimeMillis() - beginTime;
            logger.info("{}请求结束，耗时：{}ms", methodName, costMs);
        }
        return result;
    }

}
