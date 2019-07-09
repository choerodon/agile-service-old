package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/16.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueCreationNumVO {

    @ApiModelProperty(value = "创建时间")
    private String creationDay;

    @ApiModelProperty(value = "问题数量")
    private Integer issueCount;

    public void setCreationDay(String creationDay) {
        this.creationDay = creationDay;
    }

    public String getCreationDay() {
        return creationDay;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }
}
