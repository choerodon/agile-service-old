package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/27
 */
public class IssueProjectVO {

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "该项目下的问题id集合")
    private List<Long> issueIdList;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Long> getIssueIdList() {
        return issueIdList;
    }

    public void setIssueIdList(List<Long> issueIdList) {
        this.issueIdList = issueIdList;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
