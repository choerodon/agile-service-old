package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Objects;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/16
 */
public class TimeZoneWorkCalendarRefCreateDTO {

    private String workDay;

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWorkDay() {
        return workDay;
    }

    public void setWorkDay(String workDay) {
        this.workDay = workDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeZoneWorkCalendarRefCreateDTO)) {
            return false;
        }
        TimeZoneWorkCalendarRefCreateDTO that = (TimeZoneWorkCalendarRefCreateDTO) o;
        return Objects.equals(getWorkDay(), that.getWorkDay()) &&
                Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getWorkDay(), getStatus());
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
