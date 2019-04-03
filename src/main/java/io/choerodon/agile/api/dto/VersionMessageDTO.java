package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/6/4.
 */
public class VersionMessageDTO {

    @ApiModelProperty(value = "敏捷问题数量")
    private Integer agileIssueCount;

    @ApiModelProperty(value = "测试问题数量")
    private Integer testCaseCount;

    @ApiModelProperty(value = "修复版本的问题数量")
    private Integer fixIssueCount;

    @ApiModelProperty(value = "影响版本的问题数量")
    private Integer influenceIssueCount;

    @ApiModelProperty(value = "版本列表")
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
