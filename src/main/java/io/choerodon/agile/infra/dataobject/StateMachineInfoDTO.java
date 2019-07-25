package io.choerodon.agile.infra.dataobject;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/27.
 * Email: fuqianghuang01@gmail.com
 */

public class StateMachineInfoDTO {

    private Long stateMachineId;

    private String stateMachineName;

    private String stateMachineStatus;

    public String getStateMachineStatus() {
        return stateMachineStatus;
    }

    public void setStateMachineStatus(String stateMachineStatus) {
        this.stateMachineStatus = stateMachineStatus;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public String getStateMachineName() {
        return stateMachineName;
    }

    public void setStateMachineName(String stateMachineName) {
        this.stateMachineName = stateMachineName;
    }
}
