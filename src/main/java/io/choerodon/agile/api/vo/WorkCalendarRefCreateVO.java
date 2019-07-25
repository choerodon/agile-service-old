package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/18
 */
public class WorkCalendarRefCreateVO {

    @ApiModelProperty(value = "日期")
    private String workDay;

    @ApiModelProperty(value = "状态，0为放假，1为补班")
    private Integer status;

    public String getWorkDay() {
        return workDay;
    }

    public void setWorkDay(String workDay) {
        this.workDay = workDay;
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
}
