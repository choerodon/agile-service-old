package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/28
 */
public class IssueSubDTO {

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

    @ApiModelProperty(value = "父任务id")
    private Long parentIssueId;

    @ApiModelProperty(value = "故事点")
    private BigDecimal storyPoints;

    @ApiModelProperty(value = "关联的版本列表")
    private List<VersionIssueRelDTO> versionIssueRelDTOList;

    @ApiModelProperty(value = "关联的标签列表")
    private List<LabelIssueRelDTO> labelIssueRelDTOList;

    @ApiModelProperty(value = "评论列表")
    private List<IssueCommentVO> issueCommentVOList;

    @ApiModelProperty(value = "关联的问题链接列表")
    private List<IssueAttachmentVO> issueAttachmentVOList;

    @ApiModelProperty(value = "关联的模块列表")
    private List<ComponentIssueRelDTO> componentIssueRelDTOList;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "预估时间")
    private BigDecimal estimateTime;

    @ApiModelProperty(value = "剩余时间")
    private BigDecimal remainingTime;

    @ApiModelProperty(value = "活跃冲刺")
    private SprintNameDTO activeSprint;

    @ApiModelProperty(value = "已关闭的冲刺列表")
    private List<SprintNameDTO> closeSprint;

    @ApiModelProperty(value = "父任务编号")
    private String parentIssueNum;

    @ApiModelProperty(value = "经办人图标")
    private String assigneeImageUrl;

    @ApiModelProperty(value = "报告人图标")
    private String reporterImageUrl;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityDTO priorityDTO;

    @ApiModelProperty(value = "问题类型DTO")
    private IssueTypeDTO issueTypeDTO;

    @ApiModelProperty(value = "状态DTO")
    private StatusMapVO statusMapVO;

    @ApiModelProperty(value = "问题类型id")
    private Long issueTypeId;

    @ApiModelProperty(value = "优先级id")
    private Long priorityId;

    @ApiModelProperty(value = "业务类型：agile、test等")
    private String applyType;

    @ApiModelProperty(value = "创建人图标")
    private String createrImageUrl;

    @ApiModelProperty(value = "创建人名称")
    private String createrName;

    @ApiModelProperty(value = "创建人id")
    private Long createdBy;

    @ApiModelProperty(value = "创建人邮箱")
    private String createrEmail;

    public String getCreaterEmail() {
        return createrEmail;
    }

    public void setCreaterEmail(String createrEmail) {
        this.createrEmail = createrEmail;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public StatusMapVO getStatusMapVO() {
        return statusMapVO;
    }

    public void setStatusMapVO(StatusMapVO statusMapVO) {
        this.statusMapVO = statusMapVO;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
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

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
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

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getParentIssueId() {
        return parentIssueId;
    }

    public void setParentIssueId(Long parentIssueId) {
        this.parentIssueId = parentIssueId;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public List<VersionIssueRelDTO> getVersionIssueRelDTOList() {
        return versionIssueRelDTOList;
    }

    public void setVersionIssueRelDTOList(List<VersionIssueRelDTO> versionIssueRelDTOList) {
        this.versionIssueRelDTOList = versionIssueRelDTOList;
    }

    public List<LabelIssueRelDTO> getLabelIssueRelDTOList() {
        return labelIssueRelDTOList;
    }

    public void setLabelIssueRelDTOList(List<LabelIssueRelDTO> labelIssueRelDTOList) {
        this.labelIssueRelDTOList = labelIssueRelDTOList;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public SprintNameDTO getActiveSprint() {
        return activeSprint;
    }

    public void setActiveSprint(SprintNameDTO activeSprint) {
        this.activeSprint = activeSprint;
    }

    public List<SprintNameDTO> getCloseSprint() {
        return closeSprint;
    }

    public void setCloseSprint(List<SprintNameDTO> closeSprint) {
        this.closeSprint = closeSprint;
    }

    public String getParentIssueNum() {
        return parentIssueNum;
    }

    public void setParentIssueNum(String parentIssueNum) {
        this.parentIssueNum = parentIssueNum;
    }

    public List<ComponentIssueRelDTO> getComponentIssueRelDTOList() {
        return componentIssueRelDTOList;
    }

    public void setComponentIssueRelDTOList(List<ComponentIssueRelDTO> componentIssueRelDTOList) {
        this.componentIssueRelDTOList = componentIssueRelDTOList;
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

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
