package io.choerodon.agile.api.vo;


import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
public class VersionIssueRelVO implements Serializable {

    @ApiModelProperty(value = "版本id")
    private Long versionId;

    @ApiModelProperty(value = "问题id")
    private Long issueId;

    @ApiModelProperty(value = "版本名称")
    private String name;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "版本关系：fix、influence")
    private String relationType;

    @ApiModelProperty(value = "版本状态")
    private String statusCode;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}