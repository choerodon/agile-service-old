package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.api.vo.IssueTypeDTO;
import io.choerodon.agile.api.vo.StatusMapDTO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/23.
 * Email: fuqianghuang01@gmail.com
 */
public class FeatureExportDO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private String summary;

    private Long statusId;

    private Long issuetypeId;

    private BigDecimal storyPoints;

    private Long epicId;

    private String epicName;

    private String epicColor;

    private Long reporterId;

    private String reporterName;

    private String reporterImageUrl;

    private Date creationDate;

    private Date lastUpdateDate;

    private String featureType;

    private String benfitHypothesis;

    private String acceptanceCritera;

    private StatusMapDTO statusMapDTO;

    private IssueTypeDTO issueTypeDTO;

    private String activePiName;

    private String closePiName;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getBenfitHypothesis() {
        return benfitHypothesis;
    }

    public void setBenfitHypothesis(String benfitHypothesis) {
        this.benfitHypothesis = benfitHypothesis;
    }

    public String getAcceptanceCritera() {
        return acceptanceCritera;
    }

    public void setAcceptanceCritera(String acceptanceCritera) {
        this.acceptanceCritera = acceptanceCritera;
    }

    public void setStatusMapDTO(StatusMapDTO statusMapDTO) {
        this.statusMapDTO = statusMapDTO;
    }

    public StatusMapDTO getStatusMapDTO() {
        return statusMapDTO;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterImageUrl(String reporterImageUrl) {
        this.reporterImageUrl = reporterImageUrl;
    }

    public String getReporterImageUrl() {
        return reporterImageUrl;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    public void setIssuetypeId(Long issuetypeId) {
        this.issuetypeId = issuetypeId;
    }

    public Long getIssuetypeId() {
        return issuetypeId;
    }

    public String getActivePiName() {
        return activePiName;
    }

    public void setActivePiName(String activePiName) {
        this.activePiName = activePiName;
    }

    public String getClosePiName() {
        return closePiName;
    }

    public void setClosePiName(String closePiName) {
        this.closePiName = closePiName;
    }
}
