package io.choerodon.agile.infra.common.annotation;

import java.lang.annotation.*;

/**
 * 按字段更新issue时，设置是否更新
 *
 * @author dinghuang123@gmail.com
 * @since 2018/7/3
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Update {

    /**
     * 是否数据库字段，不是用true，表示忽略此字段
     */
    boolean temp() default false;

    String name() default "";
}
