package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/20
 */
public class IssueSprintDTO {

    private String sprintName;

    private String statusCode;

    private Long sprintId;

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
