package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.StatusDTO;

/**
 * @author shinan.chen
 * @date 2018/11/23
 */
public class StateMachineSchemeStatusChangeItem {
    private StatusDTO oldStatus;
    private StatusDTO newStatus;

    public StateMachineSchemeStatusChangeItem() {
    }

    public StateMachineSchemeStatusChangeItem(StatusDTO oldStatus, StatusDTO newStatus) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public StatusDTO getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(StatusDTO oldStatus) {
        this.oldStatus = oldStatus;
    }

    public StatusDTO getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(StatusDTO newStatus) {
        this.newStatus = newStatus;
    }
}
