package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
@VersionAudit
@ModifyAudit
@Table(name = "agile_work_calendar_holiday_ref")
public class WorkCalendarHolidayRefDO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long calendarId;

    private String name;

    private String holiday;

    private Integer year;

    /**
     * 状态，0为放假，1为补班
     */
    private Integer status;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkCalendarHolidayRefDO)) {
            return false;
        }
        WorkCalendarHolidayRefDO that = (WorkCalendarHolidayRefDO) o;
        return Objects.equals(getCalendarId(), that.getCalendarId()) &&
                Objects.equals(getHoliday(), that.getHoliday()) &&
                Objects.equals(getYear(), that.getYear()) &&
                Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCalendarId(), getHoliday(), getYear(), getStatus());
    }
}
