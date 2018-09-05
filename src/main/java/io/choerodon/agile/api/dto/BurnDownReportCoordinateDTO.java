package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/4
 */
public class BurnDownReportCoordinateDTO implements Serializable {

    public BurnDownReportCoordinateDTO(Integer start, Integer add, Integer done, Integer left, String name, Date startDate, Date endDate) {
        this.start = start;
        this.add = add;
        this.done = done;
        this.left = left;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private Integer start;

    private Integer add;

    private Integer done;

    private Integer left;

    private String name;

    private Date startDate;

    private Date endDate;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getAdd() {
        return add;
    }

    public void setAdd(Integer add) {
        this.add = add;
    }

    public Integer getDone() {
        return done;
    }

    public void setDone(Integer done) {
        this.done = done;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
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

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
