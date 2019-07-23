package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/11/8
 */
public class InputVO {
    @ApiModelProperty(value = "实例id（issueId）")
    private Long instanceId;
    @ApiModelProperty(value = "反射的方法")
    private String invokeCode;
    @ApiModelProperty(value = "输入数据的json")
    private String input;
    @ApiModelProperty(value = "状态机配置列表")
    private List<StateMachineConfigVO> configs;

    public String getInvokeCode() {
        return invokeCode;
    }

    public void setInvokeCode(String invokeCode) {
        this.invokeCode = invokeCode;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public List<StateMachineConfigVO> getConfigs() {
        return configs;
    }

    public void setConfigs(List<StateMachineConfigVO> configs) {
        this.configs = configs;
    }
}
