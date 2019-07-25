package io.choerodon.agile.api.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.agile.infra.utils.StringUtil;

/**
 * Created by WangZhe@choerodon.io on 2019-06-28.
 * Email: ettwz@hotmail.com
 */
public class IssueListFieldKVVO {

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

    @ApiModelProperty(value = "报告人id")
    private Long reporterId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "问题类型id")
    private Long issueTypeId;

    @ApiModelProperty(value = "经办人名称")
    private String assigneeName;

    @ApiModelProperty(value = "经办人登录名称")
    private String assigneeLoginName;

    @ApiModelProperty(value = "经办人真实名称")
    private String assigneeRealName;

    @ApiModelProperty(value = "报告人名称")
    private String reporterName;

    @ApiModelProperty(value = "报告人登录名称")
    private String reporterLoginName;

    @ApiModelProperty(value = "报告人真实名称")
    private String reporterRealName;

    @ApiModelProperty(value = "报告人图标")
    private String reporterImageUrl;

    @ApiModelProperty(value = "经办人图标")
    private String assigneeImageUrl;

    @ApiModelProperty(value = "史诗名称")
    private String epicName;

    @ApiModelProperty(value = "史诗id")
    private Long epicId;

    @ApiModelProperty(value = "史诗颜色")
    private String epicColor;

    @ApiModelProperty(value = "故事点")
    private BigDecimal storyPoints;

    @ApiModelProperty(value = "如果问题类型是特性，返回特性类别:business、enabler")
    private String featureType;

    @ApiModelProperty(value = "是否添加问题")
    private Boolean addIssue;

    @ApiModelProperty(value = "剩余时间")
    private BigDecimal remainingTime;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityVO priorityVO;

    @ApiModelProperty(value = "状态DTO")
    private StatusVO statusMapVO;

    @ApiModelProperty(value = "问题类型DTO")
    private IssueTypeVO issueTypeVO;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "关联的版本")
    private List<VersionIssueRelVO> versionIssueRelVOS;

    @ApiModelProperty(value = "关联的标签")
    private List<LabelIssueRelVO> labelIssueRelVOS;

    @ApiModelProperty(value = "冲刺列表")
    private List<IssueSprintVO> issueSprintVOS;

    @ApiModelProperty(value = "评论列表")
    private List<IssueComponentBriefVO> issueComponentBriefVOS;

    @ApiModelProperty(value = "自定义字段kv")
    private Map<String, String> foundationFieldValue;

    public List<IssueSprintVO> getIssueSprintVOS() {
        return issueSprintVOS;
    }

    public List<LabelIssueRelVO> getLabelIssueRelVOS() {
        return labelIssueRelVOS;
    }

    public void setLabelIssueRelVOS(List<LabelIssueRelVO> labelIssueRelVOS) {
        this.labelIssueRelVOS = labelIssueRelVOS;
    }

    public void setIssueSprintVOS(List<IssueSprintVO> issueSprintVOS) {
        this.issueSprintVOS = issueSprintVOS;
    }

    public List<IssueComponentBriefVO> getIssueComponentBriefVOS() {
        return issueComponentBriefVOS;
    }

    public void setIssueComponentBriefVOS(List<IssueComponentBriefVO> issueComponentBriefVOS) {
        this.issueComponentBriefVOS = issueComponentBriefVOS;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;

    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
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

    public List<VersionIssueRelVO> getVersionIssueRelVOS() {
        return versionIssueRelVOS;
    }

    public void setVersionIssueRelVOS(List<VersionIssueRelVO> versionIssueRelVOS) {
        this.versionIssueRelVOS = versionIssueRelVOS;
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

    public String getReporterImageUrl() {
        return reporterImageUrl;
    }

    public void setReporterImageUrl(String reporterImageUrl) {
        this.reporterImageUrl = reporterImageUrl;
    }

    public String getAssigneeImageUrl() {
        return assigneeImageUrl;
    }

    public void setAssigneeImageUrl(String assigneeImageUrl) {
        this.assigneeImageUrl = assigneeImageUrl;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
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

    public void setStatusVO(StatusVO statusMapVO) {
        this.statusMapVO = statusMapVO;
    }

    public StatusVO getStatusVO() {
        return statusMapVO;
    }

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
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

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getFeatureType() {
        return featureType;
    }

    public Map<String, String> getFoundationFieldValue() {
        return foundationFieldValue;
    }

    public void setFoundationFieldValue(Map<String, String> foundationFieldValue) {
        this.foundationFieldValue = foundationFieldValue;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
