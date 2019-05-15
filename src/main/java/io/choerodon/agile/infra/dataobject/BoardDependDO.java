package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
@ModifyAudit
@VersionAudit
@Table(name = "agile_board_depend")
public class BoardDependDO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private Long boardFeatureId;
    private Long dependBoardFeatureId;
    private Long piId;
    private Long programId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoardFeatureId() {
        return boardFeatureId;
    }

    public void setBoardFeatureId(Long boardFeatureId) {
        this.boardFeatureId = boardFeatureId;
    }

    public Long getDependBoardFeatureId() {
        return dependBoardFeatureId;
    }

    public void setDependBoardFeatureId(Long dependBoardFeatureId) {
        this.dependBoardFeatureId = dependBoardFeatureId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }
}

