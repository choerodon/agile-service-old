package io.choerodon.agile.api.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.agile.infra.common.utils.StringUtil;

public class IssueListTestWithSprintVersionVO {

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
    private List<VersionIssueRelVO> versionDTOList;

    @ApiModelProperty(value = "issue关联的冲刺")
    private List<IssueSprintVO> sprintDTOList;

    @ApiModelProperty(value = "故事点")
    private BigDecimal storyPoints;

    @ApiModelProperty(value = "冲刺进行期间是否有添加问题")
    private Boolean addIssue;

    @ApiModelProperty(value = "剩余时间")
    private BigDecimal remainingTime;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityVO priorityVO;

    @ApiModelProperty(value = "状态DTO")
    private StatusMapVO statusMapVO;

    @ApiModelProperty(value = "问题类型DTO")
    private IssueTypeVO issueTypeVO;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateDate;

    public IssueListTestWithSprintVersionVO(IssueListTestVO issueListTestVO) {
        this.issueId = issueListTestVO.getIssueId();
        this.issueNum = issueListTestVO.getIssueNum();
        this.typeCode = issueListTestVO.getTypeCode();
        this.assigneeId = issueListTestVO.getAssigneeId();
        this.projectId = issueListTestVO.getProjectId();
        this.issueTypeId = issueListTestVO.getIssueTypeId();
        this.assigneeName = issueListTestVO.getAssigneeName();
        this.assigneeImageUrl = issueListTestVO.getAssigneeImageUrl();
        this.epicName = issueListTestVO.getEpicName();
        this.storyPoints = issueListTestVO.getStoryPoints();
        this.addIssue = issueListTestVO.getAddIssue();
        this.remainingTime = issueListTestVO.getRemainingTime();
        this.priorityVO = issueListTestVO.getPriorityVO();
        this.statusMapVO = issueListTestVO.getStatusMapVO();
        this.issueTypeVO = issueListTestVO.getIssueTypeVO();
        this.lastUpdateDate = issueListTestVO.getLastUpdateDate();
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

    public List<VersionIssueRelVO> getVersionDTOList() {
        return versionDTOList;
    }

    public void setVersionDTOList(List<VersionIssueRelVO> versionDTOList) {
        this.versionDTOList = versionDTOList;
    }

    public List<IssueSprintVO> getSprintDTOList() {
        return sprintDTOList;
    }

    public void setSprintDTOList(List<IssueSprintVO> sprintDTOList) {
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

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setStatusMapVO(StatusMapVO statusMapVO) {
        this.statusMapVO = statusMapVO;
    }

    public StatusMapVO getStatusMapVO() {
        return statusMapVO;
    }

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
