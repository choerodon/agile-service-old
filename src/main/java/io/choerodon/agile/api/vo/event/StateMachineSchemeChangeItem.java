package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.StateMachineWithStatusVO;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/11/22
 */
public class StateMachineSchemeChangeItem {
    private Long issueTypeId;
    private Long issueCount;
    private Long oldStateMachineId;
    private Long newStateMachineId;
    private IssueTypeVO issueTypeVO;
    private StateMachineWithStatusVO oldStateMachine;
    private StateMachineWithStatusVO newStateMachine;
    private List<StateMachineSchemeStatusChangeItem> statusChangeItems;

    public StateMachineSchemeChangeItem() {
    }

    public StateMachineSchemeChangeItem(Long issueTypeId, Long oldStateMachineId, Long newStateMachineId) {
        this.issueTypeId = issueTypeId;
        this.oldStateMachineId = oldStateMachineId;
        this.newStateMachineId = newStateMachineId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public List<StateMachineSchemeStatusChangeItem> getStatusChangeItems() {
        return statusChangeItems;
    }

    public void setStatusChangeItems(List<StateMachineSchemeStatusChangeItem> statusChangeItems) {
        this.statusChangeItems = statusChangeItems;
    }

    public Long getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Long issueCount) {
        this.issueCount = issueCount;
    }

    public Long getOldStateMachineId() {
        return oldStateMachineId;
    }

    public void setOldStateMachineId(Long oldStateMachineId) {
        this.oldStateMachineId = oldStateMachineId;
    }

    public Long getNewStateMachineId() {
        return newStateMachineId;
    }

    public void setNewStateMachineId(Long newStateMachineId) {
        this.newStateMachineId = newStateMachineId;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
    }

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public StateMachineWithStatusVO getOldStateMachine() {
        return oldStateMachine;
    }

    public void setOldStateMachine(StateMachineWithStatusVO oldStateMachine) {
        this.oldStateMachine = oldStateMachine;
    }

    public StateMachineWithStatusVO getNewStateMachine() {
        return newStateMachine;
    }

    public void setNewStateMachine(StateMachineWithStatusVO newStateMachine) {
        this.newStateMachine = newStateMachine;
    }
}
