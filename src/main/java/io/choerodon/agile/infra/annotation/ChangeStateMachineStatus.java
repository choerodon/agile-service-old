package io.choerodon.agile.infra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对状态机进行编辑操作时，如果状态机为【活跃】状态，则更新状态机的状态为【草稿】
 *
 * @author shinan.chen
 * @since 2018/11/23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ChangeStateMachineStatus {
}
