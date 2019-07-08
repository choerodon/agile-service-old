package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/25
 */
public class IssueEpicDTO {

    @ApiModelProperty(value = "史诗id")
    private Long issueId;

    @ApiModelProperty(value = "史诗名称")
    private String epicName;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
