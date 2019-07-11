package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.IssueNumUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.core.oauth.DetailsHelper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public class IssueConvertDTO {

    private static final String SUB_TASK = "sub_task";
    private static final String ISSUE_EPIC = "issue_epic";
    private static final String ISSUE_FEATURE = "feature";
    private static final String DEFAULT_ASSIGNEE = "default_assignee";
    private static final String CURRENT_USER = "current_user";

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private Long statusId;

    private String summary;

    private String priorityCode;

    private Long reporterId;

    private String description;

    private String rank;

    private Long assigneeId;

    private Long projectId;

    private Long epicId;

    private Long sprintId;

    private Long parentIssueId;

    private BigDecimal storyPoints;

    private Long objectVersionNumber;

    private BigDecimal estimateTime;

    private BigDecimal remainingTime;

    private String colorCode;

    private String epicName;

    private Long originSprintId;

    private Integer epicSequence;

    private String mapRank;

    private Long priorityId;

    private Long issueTypeId;

    private String applyType;

    private Boolean assigneerCondtiion;

    public Boolean getAssigneerCondtiion() {
        return assigneerCondtiion;
    }

    public void setAssigneerCondtiion(Boolean assigneerCondtiion) {
        this.assigneerCondtiion = assigneerCondtiion;
    }

    private Date stayDate;

    private Long featureId;

    private Date startDate;

    private Date endDate;

    private Long programId;

    private Long piId;

    private Long relateIssueId;

    public Integer getEpicSequence() {
        return epicSequence;
    }

    public void setEpicSequence(Integer epicSequence) {
        this.epicSequence = epicSequence;
    }

    public Long getOriginSprintId() {
        return originSprintId;
    }

    public void setOriginSprintId(Long originSprintId) {
        this.originSprintId = originSprintId;
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

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getParentIssueId() {
        return parentIssueId;
    }

    public void setParentIssueId(Long parentIssueId) {
        this.parentIssueId = parentIssueId;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public BigDecimal getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(BigDecimal estimateTime) {
        this.estimateTime = estimateTime;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(BigDecimal remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public void setMapRank(String mapRank) {
        this.mapRank = mapRank;
    }

    public String getMapRank() {
        return mapRank;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

    /**
     * 初始化创建子任务
     *
     * @param subIssueConvertDTO subIssueConvertDTO
     * @return IssueConvertDTO
     */
    public IssueConvertDTO initializationSubIssue(IssueConvertDTO subIssueConvertDTO, Long statusId, ProjectInfoDTO projectInfoDTO) {
        subIssueConvertDTO.setAssigneerCondtiion(subIssueConvertDTO.getAssigneeId() == null);
        subIssueConvertDTO.setStatusId(statusId);
        subIssueConvertDTO.setParentIssueId(this.issueId);
        subIssueConvertDTO.setSprintId(this.sprintId);
        subIssueConvertDTO.setTypeCode(SUB_TASK);
        subIssueConvertDTO.setEpicId(0L);
        subIssueConvertDTO.initializationReporter();
        subIssueConvertDTO.initializationIssueNum(projectInfoDTO.getProjectId());
        subIssueConvertDTO.initializationIssueUser();
        subIssueConvertDTO.initializationDefaultSetting(projectInfoDTO);
        return subIssueConvertDTO;
    }

    /**
     * 对epic类型的issue初始化颜色
     *
     * @param lookupValueDTOList lookupValueDTOList
     */
    public void initializationColor(List<LookupValueDTO> lookupValueDTOList) {
        this.colorCode = lookupValueDTOList.get(new Random().nextInt(lookupValueDTOList.size())).getValueCode();
    }

    /**
     * 初始化创建issue
     *
     * @param statusId statusId
     */
    public void initializationIssue(Long statusId, ProjectInfoDTO projectInfoDTO) {
        this.statusId = statusId;
        this.parentIssueId = 0L;
        if (this.epicId == null) {
            this.epicId = 0L;
        }
        //判断是否指定了经办人
        this.assigneerCondtiion = this.assigneeId == null;
        //处理报告人
        initializationReporter();
        initializationIssueUser();
        //项目默认设置
        initializationDefaultSetting(projectInfoDTO);
        //编号设置
        initializationIssueNum(projectInfoDTO.getProjectId());
    }

    private void initializationDefaultSetting(ProjectInfoDTO projectInfoDTO) {
        if (this.assigneeId == null && projectInfoDTO.getDefaultAssigneeType() != null) {
            if (DEFAULT_ASSIGNEE.equals(projectInfoDTO.getDefaultAssigneeType())) {
                this.assigneeId = projectInfoDTO.getDefaultAssigneeId();
            } else if (CURRENT_USER.equals(projectInfoDTO.getDefaultAssigneeType())) {
                this.assigneeId = DetailsHelper.getUserDetails().getUserId();
            }
        }
        if (this.priorityCode == null && projectInfoDTO.getDefaultPriorityCode() != null) {
            this.priorityCode = projectInfoDTO.getDefaultPriorityCode();
        }
    }

    public Boolean isIssueRank() {
        return Objects.equals(this.applyType, SchemeApplyType.AGILE) && !Objects.equals(this.typeCode, ISSUE_EPIC);
    }

    public Boolean isProgramRank() {
        return Objects.equals(this.applyType, SchemeApplyType.PROGRAM) && Objects.equals(this.typeCode, ISSUE_FEATURE);
    }

    public Boolean isIssueMapRank() {
        return Objects.equals(this.applyType, SchemeApplyType.AGILE) && !Objects.equals(this.typeCode, ISSUE_EPIC) && !Objects.equals(this.typeCode, SUB_TASK);
    }

    public void initializationIssueUser() {
        if (this.assigneeId != null && this.assigneeId == 0) {
            this.assigneeId = null;
        }
    }

    private void initializationReporter() {
        if (reporterId == null) {
            this.reporterId = DetailsHelper.getUserDetails().getUserId();
        }
    }

    private void initializationIssueNum(Long projectId) {
        Long max = IssueNumUtil.getNewIssueNum(projectId);
        this.issueNum = max.toString();
    }

    public void initializationIssueByCopy(Long statusId) {
        this.statusId = statusId;
        this.parentIssueId = 0L;
        this.issueId = null;
        this.sprintId = 0L;
        this.remainingTime = BigDecimal.valueOf(0D);
        initializationIssueNum(projectId);
    }

    public void setStayDate(Date stayDate) {
        this.stayDate = stayDate;
    }

    public Date getStayDate() {
        return stayDate;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setRelateIssueId(Long relateIssueId) {
        this.relateIssueId = relateIssueId;
    }

    public Long getRelateIssueId() {
        return relateIssueId;
    }
}