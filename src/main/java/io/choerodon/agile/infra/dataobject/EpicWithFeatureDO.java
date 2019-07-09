package io.choerodon.agile.infra.dataobject;

import java.util.List;

public class EpicWithFeatureDO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private String summary;

    private String epicName;

    private Long programId;

    private String epicRank;

    private Long epicRankObjectVersionNumber;

    private List<FeatureCommonDTO> featureCommonDTOList;

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

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getEpicName() {
        return epicName;
    }

    public List<FeatureCommonDTO> getFeatureCommonDTOList() {
        return featureCommonDTOList;
    }

    public void setFeatureCommonDTOList(List<FeatureCommonDTO> featureCommonDTOList) {
        this.featureCommonDTOList = featureCommonDTOList;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getProgramId() {
        return programId;
    }

    public String getEpicRank() {
        return epicRank;
    }

    public void setEpicRank(String epicRank) {
        this.epicRank = epicRank;
    }

    public Long getEpicRankObjectVersionNumber() {
        return epicRankObjectVersionNumber;
    }

    public void setEpicRankObjectVersionNumber(Long epicRankObjectVersionNumber) {
        this.epicRankObjectVersionNumber = epicRankObjectVersionNumber;
    }
}
