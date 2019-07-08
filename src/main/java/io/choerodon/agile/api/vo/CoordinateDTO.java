package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
public class CoordinateDTO implements Serializable {

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "问题数量")
    private Integer issueCount;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
