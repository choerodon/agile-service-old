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
@Table(name = "agile_board_team")
public class BoardTeamDO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private String rank;
    private Long teamProjectId;
    private Long programId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getTeamProjectId() {
        return teamProjectId;
    }

    public void setTeamProjectId(Long teamProjectId) {
        this.teamProjectId = teamProjectId;
    }
    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }
}

