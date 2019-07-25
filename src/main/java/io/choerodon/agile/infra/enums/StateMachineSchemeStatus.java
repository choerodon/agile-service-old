package io.choerodon.agile.infra.enums;

/**
 * @author shinan.chen
 * @date 2018/11/19
 */
public class StateMachineSchemeStatus {
    private StateMachineSchemeStatus() {
    }

    /**
     * 草稿状态（发布后修改）
     */
    public static final String DRAFT = "draft";
    /**
     * 活跃状态（发布、关联项目）
     */
    public static final String ACTIVE = "active";
    /**
     * 未活跃状态（新建）
     */
    public static final String CREATE = "create";
}
