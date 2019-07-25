package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/18.
 * Email: fuqianghuang01@gmail.com
 */
public class SubFeatureVO {

    @ApiModelProperty(value = "问题主键id")
    private Long issueId;

    @ApiModelProperty(value = "问题编号")
    private String issueNum;

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
    private IssueTypeVO issueTypeVO;

    @ApiModelProperty(value = "状态DTO")
    @Transient
    private StatusVO statusMapVO;

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

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getIssueNum() {
        return issueNum;
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

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
    }

    public void setStatusVO(StatusVO statusMapVO) {
        this.statusMapVO = statusMapVO;
    }

    public StatusVO getStatusVO() {
        return statusMapVO;
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
