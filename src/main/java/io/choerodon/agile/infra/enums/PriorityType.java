package io.choerodon.agile.infra.enums;

/**
 * @author cong.cheng@hand-china.com
 */
public enum PriorityType {
    /**
     * default
     */
    STATUS_DEFAULT("1");

    private String value;

    PriorityType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
