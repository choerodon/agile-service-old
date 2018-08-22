package io.choerodon.agile.infra.common.enums;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/22
 */
public enum RedisOperation {

    /**
     * 新增
     */
    ADD("add"),

    /**
     * 移除
     */
    REMOVE("remove");

    private final String value;

    RedisOperation(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
