package io.choerodon.agile.infra.annotation;

import java.lang.annotation.*;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface DataLog {

    String type() default "";

    boolean single() default true;

}
