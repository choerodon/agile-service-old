package io.choerodon.agile.infra.dataobject;

import java.util.List;

public class BatchRemovePiDTO {

    private Long programId;

    private Long piId;

    private List<Long> issueIds;

    public BatchRemovePiDTO(Long programId, Long piId, List<Long> issueIds) {
        this.programId = programId;
        this.piId = piId;
        this.issueIds = issueIds;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getPiId() {
        return piId;
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

}
