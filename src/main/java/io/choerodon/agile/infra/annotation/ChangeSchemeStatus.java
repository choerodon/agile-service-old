package io.choerodon.agile.infra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对状态机方案进行编辑操作时，如果状态机方案为【活跃】状态，则更新状态机方案的状态为【草稿】
 *
 * @author shinan.chen
 * @since 2018/11/22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ChangeSchemeStatus {
}
