package io.choerodon.agile.infra.dataobject;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/12.
 * Email: fuqianghuang01@gmail.com
 */
public class SubFeatureDO {

    private Long issueId;

    private Long issueNum;

    private String summary;

    private Long statusId;

    private BigDecimal storyPoints;

    private Long issueTypeId;

    private String typeCode;

    private String rank;

    private Long objectVersionNumber;

    @Transient
    private Long piId;

    private String featureType;

    private String epicName;

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

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getEpicName() {
        return epicName;
    }
}
