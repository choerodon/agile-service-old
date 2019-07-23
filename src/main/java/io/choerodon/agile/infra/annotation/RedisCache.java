package io.choerodon.agile.infra.annotation;

import java.lang.annotation.*;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface RedisCache {

    enum CACHE_OPERATION {
        /**
         * 新增
         */
        ADD,
        /**
         * 删除
         */
        REMOVE
    }

    /**
     * 缓存名称数组，用于批量移除操作
     */
    String[] removeKeys() default {};

    /**
     * 缓存名称 默认加入方法名+参数名+项目id/有设置则为设置名称+项目id
     */
    String key() default "";

    /**
     * 操作 add/remove
     */
    CACHE_OPERATION operation() default CACHE_OPERATION.ADD;

    /**
     * 缓存存活时间，单位秒,为0则时效为项目运行期间
     */
    long ttl() default 0;

}
