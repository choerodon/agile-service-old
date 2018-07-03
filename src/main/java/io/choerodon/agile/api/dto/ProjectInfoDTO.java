package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/15
 */
public class ProjectInfoDTO {

    private Long infoId;

    private Long projectId;

    private String projectCode;

    private Long objectVersionNumber;

    private Long defaultAssigneeId;

    private String defaultAssigneeType;

    private String defaultPriorityCode;

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
