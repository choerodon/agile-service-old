package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.Table;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/15.
 * Email: fuqianghuang01@gmail.com
 */
@VersionAudit
@ModifyAudit
@Table(name = "agile_board_column_status_rel")
public class ColumnStatusRelDO extends AuditDomain {

    private Integer position;

    private Long statusId;

    private Long columnId;

    private Long projectId;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
