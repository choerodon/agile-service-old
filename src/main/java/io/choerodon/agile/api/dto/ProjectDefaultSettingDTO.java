package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
public class ProjectDefaultSettingDTO {

    private Long infoId;

    private Long projectId;

    private Long defaultAssigneeId;

    private String defaultPriorityCode;

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

    public Long getDefaultAssigneeId() {
        return defaultAssigneeId;
    }

    public void setDefaultAssigneeId(Long defaultAssigneeId) {
        this.defaultAssigneeId = defaultAssigneeId;
    }

    public String getDefaultPriorityCode() {
        return defaultPriorityCode;
    }

    public void setDefaultPriorityCode(String defaultPriorityCode) {
        this.defaultPriorityCode = defaultPriorityCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
