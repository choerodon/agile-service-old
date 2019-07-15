package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/16
 */
public class TimeZoneWorkCalendarRefDetailVO {

    @ApiModelProperty(value = "是否使用法定节假日")
    private Boolean useHoliday;

    @ApiModelProperty(value = "所有的周六是否上班")
    private Boolean saturdayWork;

    @ApiModelProperty(value = "所有的周日是否上班")
    private Boolean sundayWork;

    private Set<TimeZoneWorkCalendarRefCreateVO> timeZoneWorkCalendarDTOS;

    private Set<TimeZoneWorkCalendarHolidayRefVO> workHolidayCalendarDTOS;

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

    public Set<TimeZoneWorkCalendarRefCreateVO> getTimeZoneWorkCalendarDTOS() {
        return timeZoneWorkCalendarDTOS;
    }

    public void setTimeZoneWorkCalendarDTOS(Set<TimeZoneWorkCalendarRefCreateVO> timeZoneWorkCalendarDTOS) {
        this.timeZoneWorkCalendarDTOS = timeZoneWorkCalendarDTOS;
    }

    public Set<TimeZoneWorkCalendarHolidayRefVO> getWorkHolidayCalendarDTOS() {
        return workHolidayCalendarDTOS;
    }

    public void setWorkHolidayCalendarDTOS(Set<TimeZoneWorkCalendarHolidayRefVO> workHolidayCalendarDTOS) {
        this.workHolidayCalendarDTOS = workHolidayCalendarDTOS;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
