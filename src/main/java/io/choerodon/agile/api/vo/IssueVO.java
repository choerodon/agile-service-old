package io.choerodon.agile.api.vo;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public class IssueVO {

    @ApiModelProperty(value = "问题主键id")
    private Long issueId;

    @ApiModelProperty(value = "问题编号")
    private String issueNum;

    @ApiModelProperty(value = "问题类型code")
    private String typeCode;

    @ApiModelProperty(value = "状态id")
    private Long statusId;

    @ApiModelProperty(value = "问题概要")
    private String summary;

    @ApiModelProperty(value = "报告人id")
    private Long reporterId;

    @ApiModelProperty(value = "报告人名称")
    private String reporterName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "经办人id")
    private Long assigneeId;

    @ApiModelProperty(value = "经办人名称")
    private String assigneeName;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "史诗id")
    private Long epicId;

    @ApiModelProperty(value = "父任务id")
    private Long parentIssueId;

    @ApiModelProperty(value = "故事点")
    private BigDecimal storyPoints;

    @ApiModelProperty(value = "关联的版本列表")
    private List<VersionIssueRelVO> versionIssueRelVOList;

    @ApiModelProperty(value = "活跃冲刺")
    private SprintNameVO activeSprint;

    @ApiModelProperty(value = "已关闭的冲刺列表")
    private List<SprintNameVO> closeSprint;

    @ApiModelProperty(value = "活跃pi")
    private PiNameVO activePi;

    @ApiModelProperty(value = "已关闭的pi列表")
    private List<PiNameVO> closePi;

    @ApiModelProperty(value = "关联的标签列表")
    private List<LabelIssueRelVO> labelIssueRelVOList;

    @ApiModelProperty(value = "关联的模块列表")
    private List<ComponentIssueRelVO> componentIssueRelVOList;

    @ApiModelProperty(value = "评论列表")
    private List<IssueCommentVO> issueCommentVOList;

    @ApiModelProperty(value = "附件列表")
    private List<IssueAttachmentVO> issueAttachmentVOList;

    @ApiModelProperty(value = "子任务列表")
    private List<IssueSubListVO> subIssueVOList;

    @ApiModelProperty(value = "子缺陷列表")
    private List<IssueSubListVO> subBugVOList;

    @ApiModelProperty(value = "问题版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "预估时间")
    private BigDecimal estimateTime;

    @ApiModelProperty(value = "剩余时间")
    private BigDecimal remainingTime;

    @ApiModelProperty(value = "史诗名称")
    private String epicName;

    @ApiModelProperty(value = "关联史诗名称")
    private String issueEpicName;

    @ApiModelProperty(value = "史诗颜色")
    private String color;

    @ApiModelProperty(value = "史诗颜色")
    private String epicColor;

    @ApiModelProperty(value = "冲刺名称")
    private String sprintName;

    @ApiModelProperty(value = "父任务的问题编码")
    private String parentIssueNum;

    @ApiModelProperty(value = "经办人图标")
    private String assigneeImageUrl;

    @ApiModelProperty(value = "报告人图标")
    private String reporterImageUrl;

    @ApiModelProperty(value = "创建人图标")
    private String createrImageUrl;

    @ApiModelProperty(value = "创建人名称")
    private String createrName;

    @ApiModelProperty(value = "优先级id")
    private Long priorityId;

    @ApiModelProperty(value = "问题类型id")
    private Long issueTypeId;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityVO priorityVO;

    @ApiModelProperty(value = "问题类型DTO")
    private IssueTypeVO issueTypeVO;

    @ApiModelProperty(value = "状态DTO")
    private StatusMapVO statusMapVO;

    @ApiModelProperty(value = "创建人id")
    private Long createdBy;

    @ApiModelProperty(value = "业务类型：agile、test等")
    private String applyType;

    @ApiModelProperty(value = "创建人邮箱")
    private String createrEmail;

    @ApiModelProperty(value = "featureVO")
    private FeatureVO featureVO;

    @ApiModelProperty(value = "featureId")
    private Long featureId;

    @ApiModelProperty(value = "feature名称")
    private String featureName;

    @ApiModelProperty(value = "经办人登录名称")
    private String assigneeLoginName;

    @ApiModelProperty(value = "经办人真实名称")
    private String assigneeRealName;

    @ApiModelProperty(value = "报告人登录名称")
    private String reporterLoginName;

    @ApiModelProperty(value = "报告人真实名称")
    private String reporterRealName;

    @ApiModelProperty(value = "缺陷关联的故事id")
    private Long relateIssueId;

    @ApiModelProperty(value = "缺陷关联的故事编号")
    private String relateIssueNum;

    public String getCreaterEmail() {
        return createrEmail;
    }

    public void setCreaterEmail(String createrEmail) {
        this.createrEmail = createrEmail;
    }

    public String getCreaterImageUrl() {
        return createrImageUrl;
    }

    public void setCreaterImageUrl(String createrImageUrl) {
        this.createrImageUrl = createrImageUrl;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public StatusMapVO getStatusMapVO() {
        return statusMapVO;
    }

    public void setStatusMapVO(StatusMapVO statusMapVO) {
        this.statusMapVO = statusMapVO;
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

    public List<VersionIssueRelVO> getVersionIssueRelVOList() {
        return versionIssueRelVOList;
    }

    public void setVersionIssueRelVOList(List<VersionIssueRelVO> versionIssueRelVOList) {
        this.versionIssueRelVOList = versionIssueRelVOList;
    }

    public List<LabelIssueRelVO> getLabelIssueRelVOList() {
        return labelIssueRelVOList;
    }

    public void setLabelIssueRelVOList(List<LabelIssueRelVO> labelIssueRelVOList) {
        this.labelIssueRelVOList = labelIssueRelVOList;
    }

    public List<ComponentIssueRelVO> getComponentIssueRelVOList() {
        return componentIssueRelVOList;
    }

    public void setComponentIssueRelVOList(List<ComponentIssueRelVO> componentIssueRelVOList) {
        this.componentIssueRelVOList = componentIssueRelVOList;
    }

    public List<IssueCommentVO> getIssueCommentVOList() {
        return issueCommentVOList;
    }

    public void setIssueCommentVOList(List<IssueCommentVO> issueCommentVOList) {
        this.issueCommentVOList = issueCommentVOList;
    }

    public List<IssueAttachmentVO> getIssueAttachmentVOList() {
        return issueAttachmentVOList;
    }

    public void setIssueAttachmentVOList(List<IssueAttachmentVO> issueAttachmentVOList) {
        this.issueAttachmentVOList = issueAttachmentVOList;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
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

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public void setIssueEpicName(String issueEpicName) {
        this.issueEpicName = issueEpicName;
    }

    public String getIssueEpicName() {
        return issueEpicName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getParentIssueNum() {
        return parentIssueNum;
    }

    public void setParentIssueNum(String parentIssueNum) {
        this.parentIssueNum = parentIssueNum;
    }

    public SprintNameVO getActiveSprint() {
        return activeSprint;
    }

    public void setActiveSprint(SprintNameVO activeSprint) {
        this.activeSprint = activeSprint;
    }

    public List<SprintNameVO> getCloseSprint() {
        return closeSprint;
    }

    public void setCloseSprint(List<SprintNameVO> closeSprint) {
        this.closeSprint = closeSprint;
    }

    public String getAssigneeImageUrl() {
        return assigneeImageUrl;
    }

    public void setAssigneeImageUrl(String assigneeImageUrl) {
        this.assigneeImageUrl = assigneeImageUrl;
    }

    public String getReporterImageUrl() {
        return reporterImageUrl;
    }

    public void setReporterImageUrl(String reporterImageUrl) {
        this.reporterImageUrl = reporterImageUrl;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setFeatureVO(FeatureVO featureVO) {
        this.featureVO = featureVO;
    }

    public FeatureVO getFeatureVO() {
        return featureVO;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setActivePi(PiNameVO activePi) {
        this.activePi = activePi;
    }

    public PiNameVO getActivePi() {
        return activePi;
    }

    public void setClosePi(List<PiNameVO> closePi) {
        this.closePi = closePi;
    }

    public List<PiNameVO> getClosePi() {
        return closePi;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public String getAssigneeLoginName() {
        return assigneeLoginName;
    }

    public void setAssigneeLoginName(String assigneeLoginName) {
        this.assigneeLoginName = assigneeLoginName;
    }

    public String getAssigneeRealName() {
        return assigneeRealName;
    }

    public void setAssigneeRealName(String assigneeRealName) {
        this.assigneeRealName = assigneeRealName;
    }

    public String getReporterLoginName() {
        return reporterLoginName;
    }

    public void setReporterLoginName(String reporterLoginName) {
        this.reporterLoginName = reporterLoginName;
    }

    public String getReporterRealName() {
        return reporterRealName;
    }

    public void setReporterRealName(String reporterRealName) {
        this.reporterRealName = reporterRealName;
    }

    public void setRelateIssueId(Long relateIssueId) {
        this.relateIssueId = relateIssueId;
    }

    public Long getRelateIssueId() {
        return relateIssueId;
    }

    public void setRelateIssueNum(String relateIssueNum) {
        this.relateIssueNum = relateIssueNum;
    }

    public String getRelateIssueNum() {
        return relateIssueNum;
    }

    public void setSubIssueVOList(List<IssueSubListVO> subIssueVOList) {
        this.subIssueVOList = subIssueVOList;
    }

    public List<IssueSubListVO> getSubIssueVOList() {
        return subIssueVOList;
    }

    public void setSubBugVOList(List<IssueSubListVO> subBugVOList) {
        this.subBugVOList = subBugVOList;
    }

    public List<IssueSubListVO> getSubBugVOList() {
        return subBugVOList;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}