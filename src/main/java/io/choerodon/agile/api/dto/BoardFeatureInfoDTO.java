package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public class BoardFeatureInfoDTO {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "特性id")
    private Long featureId;
    @ApiModelProperty(value = "冲刺id")
    private Long sprintId;
    @ApiModelProperty(value = "piId")
    private Long piId;
    @ApiModelProperty(value = "团队项目id")
    private Long teamProjectId;
    @ApiModelProperty(value = "rank值")
    private String rank;
    @ApiModelProperty(value = "项目群id")
    private Long programId;
    @ApiModelProperty(value = "特性类型")
    private String featureType;
    @ApiModelProperty(value = "特性问题编码")
    private String issueCode;
    @ApiModelProperty(value = "问题概要")
    private String summary;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getTeamProjectId() {
        return teamProjectId;
    }

    public void setTeamProjectId(Long teamProjectId) {
        this.teamProjectId = teamProjectId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getIssueCode() {
        return issueCode;
    }

    public void setIssueCode(String issueCode) {
        this.issueCode = issueCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

