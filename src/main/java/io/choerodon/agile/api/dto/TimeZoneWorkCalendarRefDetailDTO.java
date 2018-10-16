package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Set;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/16
 */
public class TimeZoneWorkCalendarRefDetailDTO {

    private Boolean useHoliday;

    private Boolean saturdayWork;

    private Boolean sundayWork;

    private Set<TimeZoneWorkCalendarRefCreateDTO> timeZoneWorkCalendarDTOS;

    private Set<TimeZoneWorkCalendarRefCreateDTO> workHolidayCalendarDTOS;

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

    public Set<TimeZoneWorkCalendarRefCreateDTO> getTimeZoneWorkCalendarDTOS() {
        return timeZoneWorkCalendarDTOS;
    }

    public void setTimeZoneWorkCalendarDTOS(Set<TimeZoneWorkCalendarRefCreateDTO> timeZoneWorkCalendarDTOS) {
        this.timeZoneWorkCalendarDTOS = timeZoneWorkCalendarDTOS;
    }

    public Set<TimeZoneWorkCalendarRefCreateDTO> getWorkHolidayCalendarDTOS() {
        return workHolidayCalendarDTOS;
    }

    public void setWorkHolidayCalendarDTOS(Set<TimeZoneWorkCalendarRefCreateDTO> workHolidayCalendarDTOS) {
        this.workHolidayCalendarDTOS = workHolidayCalendarDTOS;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
