package io.choerodon.agile.infra.dataobject;


import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
@Table(name = "agile_issue_label")
public class IssueLabelDO extends BaseDTO {

    /***/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labelId;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 项目id
     */
    @NotNull(message = "error.IssueLabel.projectIdNotNull")
    private Long projectId;

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
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