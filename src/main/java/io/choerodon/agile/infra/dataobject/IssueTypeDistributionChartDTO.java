package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/14
 */
public class IssueTypeDistributionChartDTO {

    private String name;

    private Long  statusId;

    private Long issueTypeId;

    private Integer count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
