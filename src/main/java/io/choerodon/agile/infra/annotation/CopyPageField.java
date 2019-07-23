package io.choerodon.agile.infra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 项目层编辑pageField时，复制组织层页面配置到项目层
 *
 * @author shinan.chen
 * @since 2019/4/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface CopyPageField {
}
