package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public class TimeZoneWorkCalendarCreateDTO {

    @NotEmpty(message = "error.TimeZoneWorkCalendar.areaCode")
    private String areaCode;

    @NotEmpty(message = "error.TimeZoneWorkCalendar.timeZoneCode")
    private String timeZoneCode;

    private String workTypeCode;

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
