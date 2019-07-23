package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/19
 */
public class StateMachineTransformVO {
    @ApiModelProperty(value = "转换id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "状态机id")
    private Long stateMachineId;
    @ApiModelProperty(value = "起始节点id")
    private Long startNodeId;
    @ApiModelProperty(value = "目标节点id")
    private Long endNodeId;
    @ApiModelProperty(value = "页面路径")
    private String url;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "转换类型（transform_init/transform_all/transform_custom）")
    private String type;
    @ApiModelProperty(value = "样式")
    private String style;
    @ApiModelProperty(value = "条件策略（condition_all/condition_one）")
    private String conditionStrategy;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "目标节点的状态id")
    private Long endStatusId;
    @ApiModelProperty(value = "起始节点对象")
    private StateMachineNodeVO startNodeVO;
    @ApiModelProperty(value = "目标节点对象")
    private StateMachineNodeVO endNodeVO;
    @ApiModelProperty(value = "条件列表")
    private List<StateMachineConfigVO> conditions;
    @ApiModelProperty(value = "验证列表")
    private List<StateMachineConfigVO> validators;
    @ApiModelProperty(value = "触发器列表")
    private List<StateMachineConfigVO> triggers;
    @ApiModelProperty(value = "后置动作列表")
    private List<StateMachineConfigVO> postpositions;

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

    public Long getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(Long startNodeId) {
        this.startNodeId = startNodeId;
    }

    public Long getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(Long endNodeId) {
        this.endNodeId = endNodeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
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

    public List<StateMachineConfigVO> getConditions() {
        return conditions;
    }

    public void setConditions(List<StateMachineConfigVO> conditions) {
        this.conditions = conditions;
    }

    public List<StateMachineConfigVO> getValidators() {
        return validators;
    }

    public void setValidators(List<StateMachineConfigVO> validators) {
        this.validators = validators;
    }

    public List<StateMachineConfigVO> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<StateMachineConfigVO> triggers) {
        this.triggers = triggers;
    }

    public List<StateMachineConfigVO> getPostpositions() {
        return postpositions;
    }

    public void setPostpositions(List<StateMachineConfigVO> postpositions) {
        this.postpositions = postpositions;
    }

    public StateMachineNodeVO getStartNodeVO() {
        return startNodeVO;
    }

    public void setStartNodeVO(StateMachineNodeVO startNodeVO) {
        this.startNodeVO = startNodeVO;
    }

    public StateMachineNodeVO getEndNodeVO() {
        return endNodeVO;
    }

    public void setEndNodeVO(StateMachineNodeVO endNodeVO) {
        this.endNodeVO = endNodeVO;
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

    public Long getEndStatusId() {
        return endStatusId;
    }

    public void setEndStatusId(Long endStatusId) {
        this.endStatusId = endStatusId;
    }
}
