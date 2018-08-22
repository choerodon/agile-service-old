package io.choerodon.agile.infra.common.annotation;

import io.choerodon.agile.infra.common.enums.RedisOperation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface RedisCache {

    @AliasFor("value")
    String[] cacheNames() default {};

    String key() default "";

    String projectId() default "";

    /**
     * 操作 add/remove
     */
    RedisOperation operation() default RedisOperation.ADD;

    /**
     * 缓存存活时间，单位秒
     */
    long ttl() default 100000;

}
