package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.StateMachineConfigVO;

import java.util.List;

/**
 * 用于客户端获取某个状态的转换列表，其中节点id替换成状态id
 *
 * @author shinan.chen
 * @date 2018/10/8
 */
public class TransformInfo {
    private Long id;
    private String name;
    private String description;
    private Long stateMachineId;
    private Long startStatusId;
    private Long endStatusId;
    private String url;
    private String type;
    private String style;
    private String conditionStrategy;
    private Long organizationId;
    private Long objectVersionNumber;

    private List<StateMachineConfigVO> conditions;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public Long getStartStatusId() {
        return startStatusId;
    }

    public void setStartStatusId(Long startStatusId) {
        this.startStatusId = startStatusId;
    }

    public Long getEndStatusId() {
        return endStatusId;
    }

    public void setEndStatusId(Long endStatusId) {
        this.endStatusId = endStatusId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getConditionStrategy() {
        return conditionStrategy;
    }

    public void setConditionStrategy(String conditionStrategy) {
        this.conditionStrategy = conditionStrategy;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<StateMachineConfigVO> getConditions() {
        return conditions;
    }

    public void setConditions(List<StateMachineConfigVO> conditions) {
        this.conditions = conditions;
    }
}
