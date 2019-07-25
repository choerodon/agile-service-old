package io.choerodon.agile.infra.enums;

/**
 * @author shinan.chen
 * @date 2018/9/27
 */
public class StateMachineStatus {
    private StateMachineStatus() {
    }

    /**
     * 草稿状态（发布后修改）
     */
    public static final String DRAFT = "state_machine_draft";
    /**
     * 活跃状态（发布、关联项目）
     */
    public static final String ACTIVE = "state_machine_active";
    /**
     * 未活跃状态（新建）
     */
    public static final String CREATE = "state_machine_create";
}
