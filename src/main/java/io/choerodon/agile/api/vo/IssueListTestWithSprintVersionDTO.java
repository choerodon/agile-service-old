package io.choerodon.agile.api.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.agile.infra.common.utils.StringUtil;

public class IssueListTestWithSprintVersionDTO {

    @ApiModelProperty(value = "问题主键id")
    private Long issueId;

    @ApiModelProperty(value = "问题编号")
    private String issueNum;

    @ApiModelProperty(value = "问题类型code")
    private String typeCode;

    @ApiModelProperty(value = "问题概要")
    private String summary;

    @ApiModelProperty(value = "经办人id")
    private Long assigneeId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "问题类型id")
    private Long issueTypeId;

    @ApiModelProperty(value = "经办人名称")
    private String assigneeName;

    @ApiModelProperty(value = "经办人图标")
    private String assigneeImageUrl;

    @ApiModelProperty(value = "史诗名称")
    private String epicName;

    @ApiModelProperty(value = "issue关联的版本")
    private List<VersionIssueRelDTO> versionDTOList;

    @ApiModelProperty(value = "issue关联的冲刺")
    private List<IssueSprintDTO> sprintDTOList;

    @ApiModelProperty(value = "故事点")
    private BigDecimal storyPoints;

    @ApiModelProperty(value = "冲刺进行期间是否有添加问题")
    private Boolean addIssue;

    @ApiModelProperty(value = "剩余时间")
    private BigDecimal remainingTime;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityDTO priorityDTO;

    @ApiModelProperty(value = "状态DTO")
    private StatusMapVO statusMapVO;

    @ApiModelProperty(value = "问题类型DTO")
    private IssueTypeDTO issueTypeDTO;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateDate;

    public IssueListTestWithSprintVersionDTO(IssueListTestDTO issueListTestDTO) {
        this.issueId = issueListTestDTO.getIssueId();
        this.issueNum = issueListTestDTO.getIssueNum();
        this.typeCode = issueListTestDTO.getTypeCode();
        this.assigneeId = issueListTestDTO.getAssigneeId();
        this.projectId = issueListTestDTO.getProjectId();
        this.issueTypeId = issueListTestDTO.getIssueTypeId();
        this.assigneeName = issueListTestDTO.getAssigneeName();
        this.assigneeImageUrl = issueListTestDTO.getAssigneeImageUrl();
        this.epicName = issueListTestDTO.getEpicName();
        this.storyPoints = issueListTestDTO.getStoryPoints();
        this.addIssue = issueListTestDTO.getAddIssue();
        this.remainingTime = issueListTestDTO.getRemainingTime();
        this.priorityDTO = issueListTestDTO.getPriorityDTO();
        this.statusMapVO = issueListTestDTO.getStatusMapVO();
        this.issueTypeDTO = issueListTestDTO.getIssueTypeDTO();
        this.lastUpdateDate = issueListTestDTO.getLastUpdateDate();
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;

    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

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

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAssigneeImageUrl() {
        return assigneeImageUrl;
    }

    public void setAssigneeImageUrl(String assigneeImageUrl) {
        this.assigneeImageUrl = assigneeImageUrl;
    }

    public List<VersionIssueRelDTO> getVersionDTOList() {
        return versionDTOList;
    }

    public void setVersionDTOList(List<VersionIssueRelDTO> versionDTOList) {
        this.versionDTOList = versionDTOList;
    }

    public List<IssueSprintDTO> getSprintDTOList() {
        return sprintDTOList;
    }

    public void setSprintDTOList(List<IssueSprintDTO> sprintDTOList) {
        this.sprintDTOList = sprintDTOList;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Boolean getAddIssue() {
        return addIssue;
    }

    public void setAddIssue(Boolean addIssue) {
        this.addIssue = addIssue;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(BigDecimal remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    public void setStatusMapVO(StatusMapVO statusMapVO) {
        this.statusMapVO = statusMapVO;
    }

    public StatusMapVO getStatusMapVO() {
        return statusMapVO;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
