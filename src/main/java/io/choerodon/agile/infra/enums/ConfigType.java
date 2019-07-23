package io.choerodon.agile.infra.enums;

/**
 * @author shinan.chen
 * @date 2018/9/27
 */
public class ConfigType {
    private ConfigType() {
    }

    /**
     * 条件
     */
    public static final String CONDITION = "config_condition";
    /**
     * 验证
     */
    public static final String VALIDATOR = "config_validator";
    /**
     * 触发器
     */
    public static final String TRIGGER = "config_trigger";
    /**
     * 后置处理
     */
    public static final String ACTION = "config_action";
}
