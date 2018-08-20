package io.choerodon.agile.api.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/20.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapMoveDTO {

    @NotNull
    private Long issueId;

    @NotNull
    private Long originEpicId;

    @NotNull
    private Long epicId;

    @NotNull
    private Long objectVersionNumber;

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public Long getOriginEpicId() {
        return originEpicId;
    }

    public void setOriginEpicId(Long originEpicId) {
        this.originEpicId = originEpicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
