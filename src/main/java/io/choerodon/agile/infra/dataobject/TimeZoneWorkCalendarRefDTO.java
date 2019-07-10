package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/15
 */
@Table(name = "agile_time_zone_work_calendar_ref")
public class TimeZoneWorkCalendarRefDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long calendarId;

    private Long timeZoneId;

    private String workDay;

    private Integer year;

    private Long organizationId;

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Long getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(Long timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getWorkDay() {
        return workDay;
    }

    public void setWorkDay(String workDay) {
        this.workDay = workDay;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeZoneWorkCalendarRefDTO)) {
            return false;
        }
        TimeZoneWorkCalendarRefDTO that = (TimeZoneWorkCalendarRefDTO) o;
        return Objects.equals(getCalendarId(), that.getCalendarId()) &&
                Objects.equals(getTimeZoneId(), that.getTimeZoneId()) &&
                Objects.equals(getWorkDay(), that.getWorkDay()) &&
                Objects.equals(getYear(), that.getYear()) &&
                Objects.equals(getOrganizationId(), that.getOrganizationId()) &&
                Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCalendarId(), getTimeZoneId(), getWorkDay(), getYear(), getOrganizationId(), getStatus());
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
