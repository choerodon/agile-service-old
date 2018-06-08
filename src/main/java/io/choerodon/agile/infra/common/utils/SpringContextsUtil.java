package io.choerodon.agile.infra.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring应用上下文工具
 *
 * @author dinghuang123@gmail.com
 */
@Component
public class SpringContextsUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     *
     * @param applicationContext 应用上下文
     * @throws BeansException BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        getApplicationContext(applicationContext);
    }

    /**
     * 获取对象
     *
     * @param clazz clazz
     * @param <T>   T
     * @return T
     */
    public static <T> T getBean(Class<T> clazz) {
        return SpringContextsUtil.applicationContext.getBean(clazz);
    }

    private static void getApplicationContext(ApplicationContext applicationContext) {
        SpringContextsUtil.applicationContext = applicationContext;
    }

}