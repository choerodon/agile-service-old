package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Table(name = "agile_issue_link_type")
public class IssueLinkTypeDO extends BaseDTO {

    /***/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkTypeId;

    private String linkName;

    private String inWard;

    private String outWard;

    private Long projectId;

    public Long getLinkTypeId() {
        return linkTypeId;
    }

    public void setLinkTypeId(Long linkTypeId) {
        this.linkTypeId = linkTypeId;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getInWard() {
        return inWard;
    }

    public void setInWard(String inWard) {
        this.inWard = inWard;
    }

    public String getOutWard() {
        return outWard;
    }

    public void setOutWard(String outWard) {
        this.outWard = outWard;
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
