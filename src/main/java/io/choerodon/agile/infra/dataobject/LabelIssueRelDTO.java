package io.choerodon.agile.infra.dataobject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import io.choerodon.agile.infra.utils.StringUtil;
import io.choerodon.mybatis.entity.BaseDTO;

/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
@Table(name = "agile_label_issue_rel")
public class LabelIssueRelDTO extends BaseDTO {

    /***/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    /**
     * 标签id
     */
    @NotNull(message = "error.label_issue.label_idNotNull")
    private Long labelId;

    private Long projectId;

    @Transient
    private String labelName;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}