package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/17
 */
public class TimeZoneWorkCalendarHolidayRefDTO {

    @ApiModelProperty(value = "节假日名称")
    private String name;

    @ApiModelProperty(value = "节假日日期")
    private String holiday;

    /**
     * 状态，0为放假，1为补班
     */
    @ApiModelProperty(value = "状态，0为放假，1为补班")
    private Integer status;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkCalendarHolidayRefDTO)) {
            return false;
        }
        WorkCalendarHolidayRefDTO that = (WorkCalendarHolidayRefDTO) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getHoliday(), that.getHoliday()) &&
                Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getHoliday(), getStatus());
    }
}
