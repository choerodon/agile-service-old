package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/01/07.
 * Email: fuqianghuang01@gmail.com
 */
public class TransformDTO {
    private Long id;
    private String name;
    private Long stateMachineId;
    private Long endStatusId;

    private StatusDTO statusDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public Long getEndStatusId() {
        return endStatusId;
    }

    public void setEndStatusId(Long endStatusId) {
        this.endStatusId = endStatusId;
    }

    public StatusDTO getStatusDTO() {
        return statusDTO;
    }

    public void setStatusDTO(StatusDTO statusDTO) {
        this.statusDTO = statusDTO;
    }
}
