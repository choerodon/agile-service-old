package io.choerodon.agile.infra.enums;

/**
 * @author shinan.chen
 * @date 2018/9/27
 */
public class StatusType {
    private StatusType() {
    }

    /**
     * 准备【用于项目群】
     */
    public static final String PREPARE = "prepare";
    /**
     * 待处理
     */
    public static final String TODO = "todo";
    /**
     * 处理中
     */
    public static final String DOING = "doing";
    /**
     * 已完成
     */
    public static final String DONE = "done";
    /**
     * 无类型
     */
    public static final String NONE = "none";
}