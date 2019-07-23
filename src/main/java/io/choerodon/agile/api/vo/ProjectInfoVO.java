package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/15
 */
public class ProjectInfoVO {

    @ApiModelProperty(value = "主键id")
    private Long infoId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "项目code")
    private String projectCode;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "默认的经办人id")
    private Long defaultAssigneeId;

    @ApiModelProperty(value = "默认的经办人类型")
    private String defaultAssigneeType;

    @ApiModelProperty(value = "默认的优先级code")
    private String defaultPriorityCode;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getDefaultAssigneeId() {
        return defaultAssigneeId;
    }

    public void setDefaultAssigneeId(Long defaultAssigneeId) {
        this.defaultAssigneeId = defaultAssigneeId;
    }

    public String getDefaultAssigneeType() {
        return defaultAssigneeType;
    }

    public void setDefaultAssigneeType(String defaultAssigneeType) {
        this.defaultAssigneeType = defaultAssigneeType;
    }

    public String getDefaultPriorityCode() {
        return defaultPriorityCode;
    }

    public void setDefaultPriorityCode(String defaultPriorityCode) {
        this.defaultPriorityCode = defaultPriorityCode;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
