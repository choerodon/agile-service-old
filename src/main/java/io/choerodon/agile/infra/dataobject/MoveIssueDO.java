package io.choerodon.agile.infra.dataobject;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class MoveIssueDO {
    private Long issueId;
    private String rank;

    public MoveIssueDO(Long issueId, String rank) {
        this.issueId = issueId;
        this.rank = rank;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

}
