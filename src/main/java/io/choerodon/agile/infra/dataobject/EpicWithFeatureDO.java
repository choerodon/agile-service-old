package io.choerodon.agile.infra.dataobject;

import java.util.List;

public class EpicWithFeatureDO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private String summary;

    private String epicName;

    private List<FeatureCommonDO> featureCommonDOList;

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

    public List<FeatureCommonDO> getFeatureCommonDOList() {
        return featureCommonDOList;
    }

    public void setFeatureCommonDOList(List<FeatureCommonDO> featureCommonDOList) {
        this.featureCommonDOList = featureCommonDOList;
    }
}
