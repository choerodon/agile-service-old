package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/6/4.
 */
public class VersionMessageDTO {
    private Integer fixIssueCount;
    private Integer influenceIssueCount;
    private List<ProductVersionNameDTO> versionNames;

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
}
