package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/27
 */
public class PieChartDTO {

    private String name;

    private String typeName;

    private Double percent;

    private Integer value;

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
