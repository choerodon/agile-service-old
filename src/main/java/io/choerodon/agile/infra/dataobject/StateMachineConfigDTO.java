package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Table(name = "fd_state_machine_config")
public class StateMachineConfigDTO extends BaseDTO {
    private Long id;
    private Long transformId;
    private Long stateMachineId;
    private String code;
    private String type;

    @Transient
    private String codeName;
    @Transient
    private String codeDescription;

    private Long organizationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransformId() {
        return transformId;
    }

    public void setTransformId(Long transformId) {
        this.transformId = transformId;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }
}
