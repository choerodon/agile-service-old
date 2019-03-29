package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/18.
 * Email: fuqianghuang01@gmail.com
 */
public class SubFeatureDTO {

    @ApiModelProperty(value = "问题主键id")
    private Long issueId;

    @ApiModelProperty(value = "问题编号")
    private Long issueNum;

    @ApiModelProperty(value = "问题概要")
    private String summary;

    @ApiModelProperty(value = "问题状态id")
    private Long statusId;

    @ApiModelProperty(value = "故事点")
    private BigDecimal storyPoints;

    @ApiModelProperty(value = "问题类型id")
    private Long issueTypeId;

    @ApiModelProperty(value = "问题类型code")
    private String typeCode;

    @ApiModelProperty(value = "排序字段")
    private String rank;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "pi主键id")
    @Transient
    private Long piId;

    @ApiModelProperty(value = "问题类型DTO")
    @Transient
    private IssueTypeDTO issueTypeDTO;

    @ApiModelProperty(value = "状态DTO")
    @Transient
    private StatusMapDTO statusMapDTO;

    @ApiModelProperty(value = "feature的类型")
    private String featureType;

    @ApiModelProperty(value = "feature所属史诗名称")
    private String epicName;

    @ApiModelProperty(value = "feature所属史诗颜色")
    private String epicColor;

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public Long getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Long issueNum) {
        this.issueNum = issueNum;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    public void setStatusMapDTO(StatusMapDTO statusMapDTO) {
        this.statusMapDTO = statusMapDTO;
    }

    public StatusMapDTO getStatusMapDTO() {
        return statusMapDTO;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public String getEpicColor() {
        return epicColor;
    }
}
