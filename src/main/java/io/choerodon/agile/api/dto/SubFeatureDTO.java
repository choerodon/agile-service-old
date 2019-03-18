package io.choerodon.agile.api.dto;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/18.
 * Email: fuqianghuang01@gmail.com
 */
public class SubFeatureDTO {

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

    @Transient
    private IssueTypeDTO issueTypeDTO;

    @Transient
    private StatusMapDTO statusDTO;

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

    public void setStatusDTO(StatusMapDTO statusDTO) {
        this.statusDTO = statusDTO;
    }

    public StatusMapDTO getStatusDTO() {
        return statusDTO;
    }
}
