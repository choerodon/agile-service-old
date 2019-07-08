package io.choerodon.agile.api.vo.event;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/11/22
 */
public class StateMachineSchemeChangeItem {
    private Long issueTypeId;
    private List<StateMachineSchemeStatusChangeItem> statusChangeItems;

    public StateMachineSchemeChangeItem() {
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
}
