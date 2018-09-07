package io.choerodon.agile.infra.dataobject;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/6.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapMoveIssueDO {
    private Long issueId;
    private String mapRank;

    public StoryMapMoveIssueDO(Long issueId, String mapRank) {
        this.issueId = issueId;
        this.mapRank = mapRank;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setMapRank(String mapRank) {
        this.mapRank = mapRank;
    }

    public String getMapRank() {
        return mapRank;
    }
}
