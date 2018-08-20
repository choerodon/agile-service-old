package io.choerodon.agile.api.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/20.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapMoveDTO {

    @NotNull(message = "error.issueId.isNull")
    private Long issueId;

    @NotNull(message = "error.originEpicId.isNull")
    private Long originEpicId;

    @NotNull(message = "error.epicId.isNull")
    private Long epicId;

    @NotNull(message = "error.objectVersionNumber.isNull")
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
