package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/13.
 * Email: fuqianghuang01@gmail.com
 */
public class FeatureDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "问题id")
    private Long issueId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "特性价值")
    private String benfitHypothesis;

    @ApiModelProperty(value = "验收标准")
    private String acceptanceCritera;

    @ApiModelProperty(value = "特性类型")
    private String featureType;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "项目群id")
    private Long programId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getBenfitHypothesis() {
        return benfitHypothesis;
    }

    public void setBenfitHypothesis(String benfitHypothesis) {
        this.benfitHypothesis = benfitHypothesis;
    }

    public void setAcceptanceCritera(String acceptanceCritera) {
        this.acceptanceCritera = acceptanceCritera;
    }

    public String getAcceptanceCritera() {
        return acceptanceCritera;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getProgramId() {
        return programId;
    }
}
