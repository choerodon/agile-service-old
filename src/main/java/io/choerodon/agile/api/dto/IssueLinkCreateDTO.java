package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public class IssueLinkCreateDTO {

    @ApiModelProperty(value = "问题链接类型id")
    private Long linkTypeId;

    @ApiModelProperty(value = "被链接的问题id")
    private Long linkedIssueId;

    @ApiModelProperty(value = "链接问题id")
    private Long issueId;

    @ApiModelProperty(value = "正向或反向")
    private Boolean in;

    public Boolean getIn() {
        return in;
    }

    public void setIn(Boolean in) {
        this.in = in;
    }

    public Long getLinkTypeId() {
        return linkTypeId;
    }

    public void setLinkTypeId(Long linkTypeId) {
        this.linkTypeId = linkTypeId;
    }

    public Long getLinkedIssueId() {
        return linkedIssueId;
    }

    public void setLinkedIssueId(Long linkedIssueId) {
        this.linkedIssueId = linkedIssueId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
