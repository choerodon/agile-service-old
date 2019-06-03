package io.choerodon.agile.api.dto;


import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapDragDTO {

    @ApiModelProperty(value = "要关联的史诗id")
    private Long epicId;

    @ApiModelProperty(value = "要关联的特性id")
    private Long featureId;

    @ApiModelProperty(value = "要关联的版本id")
    private Long versionId;

    @ApiModelProperty(value = "要移动的问题id列表")
    private List<Long> issueIds;

    @ApiModelProperty(value = "问题id列表，移动到史诗，配合epicId使用")
    private List<Long> epicIssueIds;

    @ApiModelProperty(value = "问题id列表，移动到特性，配合featureId使用")
    private List<Long> featureIssueIds;

    @ApiModelProperty(value = "问题id列表，移动到版本，配合versionId使用")
    private List<Long> versionIssueIds;

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

    public List<Long> getEpicIssueIds() {
        return epicIssueIds;
    }

    public void setEpicIssueIds(List<Long> epicIssueIds) {
        this.epicIssueIds = epicIssueIds;
    }

    public List<Long> getFeatureIssueIds() {
        return featureIssueIds;
    }

    public void setFeatureIssueIds(List<Long> featureIssueIds) {
        this.featureIssueIds = featureIssueIds;
    }

    public List<Long> getVersionIssueIds() {
        return versionIssueIds;
    }

    public void setVersionIssueIds(List<Long> versionIssueIds) {
        this.versionIssueIds = versionIssueIds;
    }
}
