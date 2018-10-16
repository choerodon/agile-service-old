package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/15
 */
public class TimeZoneWorkCalendarUpdateDTO {

    private String areaCode;

    private String timeZoneCode;

    private String workTypeCode;

    private Long objectVersionNumber;

    private Boolean useHoliday;

    private Boolean saturdayWork;

    private Boolean sundayWork;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public String getWorkTypeCode() {
        return workTypeCode;
    }

    public void setWorkTypeCode(String workTypeCode) {
        this.workTypeCode = workTypeCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
