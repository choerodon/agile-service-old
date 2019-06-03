package io.choerodon.agile.infra.dataobject;

import java.util.List;

public class StoryMapStoryDO {

    private Long issueId;

    private String issueNum;

    private String summary;

    private List<StoryMapVersionDO> storyMapVersionDOList;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setStoryMapVersionDOList(List<StoryMapVersionDO> storyMapVersionDOList) {
        this.storyMapVersionDOList = storyMapVersionDOList;
    }

    public List<StoryMapVersionDO> getStoryMapVersionDOList() {
        return storyMapVersionDOList;
    }
}
