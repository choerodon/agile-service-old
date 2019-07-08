package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/4
 */
public class BurnDownReportCoordinateDTO implements Serializable {

    public BurnDownReportCoordinateDTO(BigDecimal start, BigDecimal add, BigDecimal done, BigDecimal left, String name, Date startDate, Date endDate) {
        this.start = start;
        this.add = add;
        this.done = done;
        this.left = left;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @ApiModelProperty(value = "开始的故事点之和")
    private BigDecimal start;

    @ApiModelProperty(value = "增加的故事点之和")
    private BigDecimal add;

    @ApiModelProperty(value = "完成的故事点之和")
    private BigDecimal done;

    @ApiModelProperty(value = "剩余的故事点之和")
    private BigDecimal left;

    @ApiModelProperty(value = "横坐标的名称")
    private String name;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    public void setStart(BigDecimal start) {
        this.start = start;
    }

    public BigDecimal getStart() {
        return start;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getAdd() {
        return add;
    }

    public void setAdd(BigDecimal add) {
        this.add = add;
    }

    public BigDecimal getDone() {
        return done;
    }

    public void setDone(BigDecimal done) {
        this.done = done;
    }

    public BigDecimal getLeft() {
        return left;
    }

    public void setLeft(BigDecimal left) {
        this.left = left;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
