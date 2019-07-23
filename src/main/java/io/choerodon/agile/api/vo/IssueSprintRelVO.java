package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
public class IssueSprintRelVO {

    @ApiModelProperty(value = "问题id")
    private Long issueId;

    @ApiModelProperty(value = "冲刺id")
    private Long sprintId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
