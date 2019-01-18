package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

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

    private BigDecimal start;

    private BigDecimal add;

    private BigDecimal done;

    private BigDecimal left;

    private String name;

    private Date startDate;

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
