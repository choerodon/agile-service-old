package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
public class ColumnChangeDTO implements Serializable {

    @ApiModelProperty(value = "问题主键id")
    private Long issueId;

    @ApiModelProperty(value = "问题编号")
    private String issueNum;

    @ApiModelProperty(value = "起始列")
    private String columnFrom;

    @ApiModelProperty(value = "目标列")
    private String columnTo;

    @ApiModelProperty(value = "目标状态")
    private String statusTo;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "旧值")
    private String oldValue;

    @ApiModelProperty(value = "新值")
    private String newValue;

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getColumnFrom() {
        return columnFrom;
    }

    public void setColumnFrom(String columnFrom) {
        this.columnFrom = columnFrom;
    }

    public String getColumnTo() {
        return columnTo;
    }

    public void setColumnTo(String columnTo) {
        this.columnTo = columnTo;
    }

    public String getStatusTo() {
        return statusTo;
    }

    public void setStatusTo(String statusTo) {
        this.statusTo = statusTo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
