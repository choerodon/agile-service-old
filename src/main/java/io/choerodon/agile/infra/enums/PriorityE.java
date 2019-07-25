package io.choerodon.agile.infra.enums;

/**
 * @author jiameng.cao
 * @date 2018/9/11
 */
public enum  PriorityE {
    HIGH("high"),
    MIDDLE("middle"),
    LOW("low");

    private String typeName;

    PriorityE(String typeName) {
        this.typeName = typeName;
    }

    public String value() {
        return this.typeName;
    }

    public static Boolean contain(String typeName) {
        for (PriorityE priority : PriorityE.values()) {
            if (priority.value().equals(typeName)) {
                return true;
            }
        }
        return false;
    }
}
