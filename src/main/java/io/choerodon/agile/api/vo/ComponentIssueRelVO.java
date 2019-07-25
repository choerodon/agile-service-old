package io.choerodon.agile.api.vo;


import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:47:27
 */
public class ComponentIssueRelVO {

    @ApiModelProperty(value = "模块id")
    private Long componentId;

    @ApiModelProperty(value = "问题id")
    private Long issueId;

    @ApiModelProperty(value = "模块名称")
    private String name;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}