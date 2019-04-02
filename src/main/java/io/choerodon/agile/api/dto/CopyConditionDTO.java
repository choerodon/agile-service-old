package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
public class CopyConditionDTO {

    @ApiModelProperty(value = "问题概要")
    private String summary;

    @ApiModelProperty(value = "是否复制子任务")
    private Boolean subTask;

    @ApiModelProperty(value = "是否复制问题链接")
    private Boolean issueLink;

    @ApiModelProperty(value = "是否复制冲刺")
    private Boolean sprintValues;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getSubTask() {
        return subTask;
    }

    public void setSubTask(Boolean subTask) {
        this.subTask = subTask;
    }

    public Boolean getIssueLink() {
        return issueLink;
    }

    public void setIssueLink(Boolean issueLink) {
        this.issueLink = issueLink;
    }

    public Boolean getSprintValues() {
        return sprintValues;
    }

    public void setSprintValues(Boolean sprintValues) {
        this.sprintValues = sprintValues;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
