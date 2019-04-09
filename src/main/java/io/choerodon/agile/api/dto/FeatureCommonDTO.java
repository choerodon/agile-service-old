package io.choerodon.agile.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/9.
 * Email: fuqianghuang01@gmail.com
 */
public class FeatureCommonDTO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private String summary;

    private Long statusId;

    private BigDecimal storyPoints;

    private String featureType;

    private String benfitHypothesis;

    private String acceptanceCritera;

    private StatusMapDTO statusMapDTO;

    private List<PiNameDTO> piNameDTOList;

    private List<VersionNameDTO> versionNameDTOList;

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

    public void setPiNameDTOList(List<PiNameDTO> piNameDTOList) {
        this.piNameDTOList = piNameDTOList;
    }

    public List<PiNameDTO> getPiNameDTOList() {
        return piNameDTOList;
    }

    public void setVersionNameDTOList(List<VersionNameDTO> versionNameDTOList) {
        this.versionNameDTOList = versionNameDTOList;
    }

    public List<VersionNameDTO> getVersionNameDTOList() {
        return versionNameDTOList;
    }

    public void setStatusMapDTO(StatusMapDTO statusMapDTO) {
        this.statusMapDTO = statusMapDTO;
    }

    public StatusMapDTO getStatusMapDTO() {
        return statusMapDTO;
    }
}
