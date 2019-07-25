package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */
public class ProjectConfigDetailVO {
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "关联的问题类型方案Map，key为applyType（应用类型）")
    private Map<String, IssueTypeSchemeVO> issueTypeSchemeMap;
    @ApiModelProperty(value = "关联的状态机方案Map，key为applyType（应用类型）")
    private Map<String, StateMachineSchemeVO> stateMachineSchemeMap;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Map<String, IssueTypeSchemeVO> getIssueTypeSchemeMap() {
        return issueTypeSchemeMap;
    }

    public void setIssueTypeSchemeMap(Map<String, IssueTypeSchemeVO> issueTypeSchemeMap) {
        this.issueTypeSchemeMap = issueTypeSchemeMap;
    }

    public Map<String, StateMachineSchemeVO> getStateMachineSchemeMap() {
        return stateMachineSchemeMap;
    }

    public void setStateMachineSchemeMap(Map<String, StateMachineSchemeVO> stateMachineSchemeMap) {
        this.stateMachineSchemeMap = stateMachineSchemeMap;
    }
}
