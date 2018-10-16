package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@VersionAudit
@ModifyAudit
@Table(name = "agile_time_zone_work_calendar")
public class TimeZoneWorkCalendarDO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long timeZoneId;

    private String areaCode;

    private String timeZoneCode;

    private Long organizationId;

    private Boolean useHoliday;

    private Boolean saturdayWork;

    private Boolean sundayWork;

    @Transient
    private List<TimeZoneWorkCalendarRefDO>  timeZoneWorkCalendarRefDOS;

    public List<TimeZoneWorkCalendarRefDO> getTimeZoneWorkCalendarRefDOS() {
        return timeZoneWorkCalendarRefDOS;
    }

    public void setTimeZoneWorkCalendarRefDOS(List<TimeZoneWorkCalendarRefDO> timeZoneWorkCalendarRefDOS) {
        this.timeZoneWorkCalendarRefDOS = timeZoneWorkCalendarRefDOS;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(Long timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getTimeZoneCode() {
        return timeZoneCode;
    }

    public void setTimeZoneCode(String timeZoneCode) {
        this.timeZoneCode = timeZoneCode;
    }

    public Boolean getUseHoliday() {
        return useHoliday;
    }

    public void setUseHoliday(Boolean useHoliday) {
        this.useHoliday = useHoliday;
    }

    public Boolean getSaturdayWork() {
        return saturdayWork;
    }

    public void setSaturdayWork(Boolean saturdayWork) {
        this.saturdayWork = saturdayWork;
    }

    public Boolean getSundayWork() {
        return sundayWork;
    }

    public void setSundayWork(Boolean sundayWork) {
        this.sundayWork = sundayWork;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
