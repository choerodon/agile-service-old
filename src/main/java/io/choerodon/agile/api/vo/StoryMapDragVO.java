package io.choerodon.agile.api.vo;


import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapDragVO {

    @ApiModelProperty(value = "要关联的史诗id")
    private Long epicId;

    @ApiModelProperty(value = "要关联的特性id")
    private Long featureId;

    @ApiModelProperty(value = "要关联的版本id")
    private Long versionId;

    @ApiModelProperty(value = "问题id列表，移动到史诗，配合epicId使用")
    private List<Long> epicIssueIds;

    @ApiModelProperty(value = "问题id列表，移动到特性，配合featureId使用")
    private List<Long> featureIssueIds;

    @ApiModelProperty(value = "问题id列表，移动到版本，配合versionId使用")
    private List<Long> versionIssueIds;

    @ApiModelProperty(value = "要删除的版本与问题关联数据")
    private List<VersionIssueRelVO> versionIssueRelVOList;

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

    public void setVersionIssueRelVOList(List<VersionIssueRelVO> versionIssueRelVOList) {
        this.versionIssueRelVOList = versionIssueRelVOList;
    }

    public List<VersionIssueRelVO> getVersionIssueRelVOList() {
        return versionIssueRelVOList;
    }
}
