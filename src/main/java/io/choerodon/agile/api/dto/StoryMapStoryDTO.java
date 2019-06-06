package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.dataobject.StoryMapVersionDO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/6.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapStoryDTO {

    private Long issueId;

    private String issueNum;

    private String summary;

    private Long epicId;

    private Long featureId;

    private Long issueTypeId;

    private Long statusId;

    private IssueTypeDTO issueTypeDTO;

    private StatusMapDTO statusMapDTO;

    private List<StoryMapVersionDTO> storyMapVersionDTOList;

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

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public void setStoryMapVersionDTOList(List<StoryMapVersionDTO> storyMapVersionDTOList) {
        this.storyMapVersionDTOList = storyMapVersionDTOList;
    }

    public List<StoryMapVersionDTO> getStoryMapVersionDTOList() {
        return storyMapVersionDTOList;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public StatusMapDTO getStatusMapDTO() {
        return statusMapDTO;
    }

    public void setStatusMapDTO(StatusMapDTO statusMapDTO) {
        this.statusMapDTO = statusMapDTO;
    }
}
