package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/6/4.
 */
public class VersionMessageDTO {
    private Integer issueCount;
    private List<ProductVersionNameDTO> versionNames;

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public List<ProductVersionNameDTO> getVersionNames() {
        return versionNames;
    }

    public void setVersionNames(List<ProductVersionNameDTO> versionNames) {
        this.versionNames = versionNames;
    }
}
