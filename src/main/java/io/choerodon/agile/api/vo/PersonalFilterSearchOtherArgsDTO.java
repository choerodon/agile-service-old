package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchOtherArgsDTO {

    @ApiModelProperty(value = "冲刺id集合")
    private List<Long> sprint;

    @ApiModelProperty(value = "版本id集合")
    private List<Long> version;

    @ApiModelProperty(value = "模块id集合")
    private List<Long> component;

    @ApiModelProperty(value = "史诗id集合")
    private List<Long> epic;

    @ApiModelProperty(value = "标签id集合")
    private List<Long> label;
    /**
     * 只用于未分配的经办人，即assigneeId=[0]
     */
    @ApiModelProperty(value = "只用于未分配的经办人，即assigneeId=[0]")
    private List<Long> assigneeId;

    public List<Long> getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(List<Long> assigneeId) {
        this.assigneeId = assigneeId;
    }

    public List<Long> getVersion() {
        return version;
    }

    public void setVersion(List<Long> version) {
        this.version = version;
    }

    public List<Long> getComponent() {
        return component;
    }

    public void setComponent(List<Long> component) {
        this.component = component;
    }

    public List<Long> getEpic() {
        return epic;
    }

    public void setEpic(List<Long> epic) {
        this.epic = epic;
    }

    public List<Long> getLabel() {
        return label;
    }

    public void setLabel(List<Long> label) {
        this.label = label;
    }

    public List<Long> getSprint() {
        return sprint;
    }

    public void setSprint(List<Long> sprint) {
        this.sprint = sprint;
    }
}
