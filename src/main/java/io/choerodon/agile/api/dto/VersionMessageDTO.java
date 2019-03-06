package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/6/4.
 */
public class VersionMessageDTO {
    private Integer agileIssueCount;
    private Integer testCaseCount;
    private Integer fixIssueCount;
    private Integer influenceIssueCount;
    private List<ProductVersionNameDTO> versionNames;

    public Integer getAgileIssueCount() {
        return agileIssueCount;
    }

    public void setAgileIssueCount(Integer agileIssueCount) {
        this.agileIssueCount = agileIssueCount;
    }

    public Integer getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(Integer testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public Integer getInfluenceIssueCount() {
        return influenceIssueCount;
    }

    public void setInfluenceIssueCount(Integer influenceIssueCount) {
        this.influenceIssueCount = influenceIssueCount;
    }

    public Integer getFixIssueCount() {
        return fixIssueCount;
    }

    public void setFixIssueCount(Integer fixIssueCount) {
        this.fixIssueCount = fixIssueCount;
    }

    public List<ProductVersionNameDTO> getVersionNames() {
        return versionNames;
    }

    public void setVersionNames(List<ProductVersionNameDTO> versionNames) {
        this.versionNames = versionNames;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
