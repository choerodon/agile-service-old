package io.choerodon.agile.infra.annotation;

import java.lang.annotation.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface FeedbackDataLog {

    String type() default "";

    boolean single() default true;

}
