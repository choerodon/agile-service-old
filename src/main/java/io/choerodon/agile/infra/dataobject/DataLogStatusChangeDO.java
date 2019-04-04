package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author dinghuang123@gmail.com
 * @since 2019/1/9
 */
public class DataLogStatusChangeDO {

    private Long logId;

    private Long newValue;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getNewValue() {
        return newValue;
    }

    public void setNewValue(Long newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataLogStatusChangeDO)) {
            return false;
        }
        DataLogStatusChangeDO that = (DataLogStatusChangeDO) o;
        return new EqualsBuilder()
                .append(getLogId(), that.getLogId())
                .append(getNewValue(), that.getNewValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getLogId())
                .append(getNewValue())
                .toHashCode();
    }
}
