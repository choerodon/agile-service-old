package io.choerodon.agile.infra.enums;

/**
 * @author shinan.chen
 * @date 2018/9/27
 */
public class NodeType {
    private NodeType() {
    }

    /**
     * 默认初始化节点
     */
    public static final String INIT = "node_init";
    /**
     * 默认开始节点（圆圈）
     */
    public static final String START = "node_start";
    /**
     * 自定义
     */
    public static final String CUSTOM = "node_custom";
}
