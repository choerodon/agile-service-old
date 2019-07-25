package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineSchemeConfigViewVO {
    @ApiModelProperty(value = "状态机DTO")
    private StateMachineVO stateMachineVO;
    @ApiModelProperty(value = "问题类型列表")
    private List<IssueTypeVO> issueTypeVOS;

    public StateMachineVO getStateMachineVO() {
        return stateMachineVO;
    }

    public void setStateMachineVO(StateMachineVO stateMachineVO) {
        this.stateMachineVO = stateMachineVO;
    }

    public List<IssueTypeVO> getIssueTypeVOS() {
        return issueTypeVOS;
    }

    public void setIssueTypeVOS(List<IssueTypeVO> issueTypeVOS) {
        this.issueTypeVOS = issueTypeVOS;
    }
}

