//package io.choerodon.agile.api.vo;
//
//import io.swagger.annotations.ApiModelProperty;
//
//import java.math.BigDecimal;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/8/8.
// * Email: fuqianghuang01@gmail.com
// */
//public class StoryMapIssueDTO {
//
//    @ApiModelProperty(value = "问题所属的冲刺id")
//    private Long sprintId;
//
//    @ApiModelProperty(value = "问题所属的冲刺名称")
//    private String sprintName;
//
//    @ApiModelProperty(value = "问题所属的版本id")
//    private Long versionId;
//
//    @ApiModelProperty(value = "问题所属的版本名称")
//    private String versionName;
//
//    @ApiModelProperty(value = "问题主键id")
//    private Long issueId;
//
//    @ApiModelProperty(value = "经办人id")
//    private Long assigneeId;
//
//    @ApiModelProperty(value = "优先级code")
//    private String priorityCode;
//
//    @ApiModelProperty(value = "优先级概要")
//    private String summary;
//
//    @ApiModelProperty(value = "问题类型code")
//    private String typeCode;
//
//    @ApiModelProperty(value = "故事点")
//    private BigDecimal storyPoints;
//
//    @ApiModelProperty(value = "问题编号")
//    private String issueNum;
//
//    @ApiModelProperty(value = "状态code")
//    private String statusCode;
//
//    @ApiModelProperty(value = "状态名称")
//    private String statusName;
//
//    @ApiModelProperty(value = "经办人名称")
//    private String assigneeName;
//
//    @ApiModelProperty(value = "经办人图标")
//    private String imageUrl;
//
//    @ApiModelProperty(value = "状态颜色")
//    private String statusColor;
//
//    @ApiModelProperty(value = "关联的史诗id")
//    private Long epicId;
//
//    @ApiModelProperty(value = "版本号")
//    private Long objectVersionNumber;
//
//    @ApiModelProperty(value = "故事地铁排序字段")
//    private String mapRank;
//
//    @ApiModelProperty(value = "优先级DTO")
//    private PriorityVO priorityDTO;
//
//    @ApiModelProperty(value = "问题类型DTO")
//    private IssueTypeVO issueTypeDTO;
//
//    @ApiModelProperty(value = "状态id")
//    private Long statusId;
//
//    @ApiModelProperty(value = "状态DTO")
//    private StatusMapVO statusMapVO;
//
//    public Long getSprintId() {
//        return sprintId;
//    }
//
//    public void setSprintId(Long sprintId) {
//        this.sprintId = sprintId;
//    }
//
//    public String getSprintName() {
//        return sprintName;
//    }
//
//    public void setSprintName(String sprintName) {
//        this.sprintName = sprintName;
//    }
//
//    public Long getIssueId() {
//        return issueId;
//    }
//
//    public void setIssueId(Long issueId) {
//        this.issueId = issueId;
//    }
//
//    public Long getAssigneeId() {
//        return assigneeId;
//    }
//
//    public void setAssigneeId(Long assigneeId) {
//        this.assigneeId = assigneeId;
//    }
//
//    public String getAssigneeName() {
//        return assigneeName;
//    }
//
//    public void setAssigneeName(String assigneeName) {
//        this.assigneeName = assigneeName;
//    }
//
//    public String getPriorityCode() {
//        return priorityCode;
//    }
//
//    public void setPriorityCode(String priorityCode) {
//        this.priorityCode = priorityCode;
//    }
//
//    public String getSummary() {
//        return summary;
//    }
//
//    public void setSummary(String summary) {
//        this.summary = summary;
//    }
//
//    public String getTypeCode() {
//        return typeCode;
//    }
//
//    public void setTypeCode(String typeCode) {
//        this.typeCode = typeCode;
//    }
//
//    public void setStoryPoints(BigDecimal storyPoints) {
//        this.storyPoints = storyPoints;
//    }
//
//    public BigDecimal getStoryPoints() {
//        return storyPoints;
//    }
//
//    public String getIssueNum() {
//        return issueNum;
//    }
//
//    public void setIssueNum(String issueNum) {
//        this.issueNum = issueNum;
//    }
//
//    public void setStatusCode(String statusCode) {
//        this.statusCode = statusCode;
//    }
//
//    public String getStatusCode() {
//        return statusCode;
//    }
//
//    public Long getVersionId() {
//        return versionId;
//    }
//
//    public void setVersionId(Long versionId) {
//        this.versionId = versionId;
//    }
//
//    public String getVersionName() {
//        return versionName;
//    }
//
//    public void setVersionName(String versionName) {
//        this.versionName = versionName;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setStatusColor(String statusColor) {
//        this.statusColor = statusColor;
//    }
//
//    public String getStatusColor() {
//        return statusColor;
//    }
//
//    public void setStatusName(String statusName) {
//        this.statusName = statusName;
//    }
//
//    public String getStatusName() {
//        return statusName;
//    }
//
//    public void setEpicId(Long epicId) {
//        this.epicId = epicId;
//    }
//
//    public Long getEpicId() {
//        return epicId;
//    }
//
//    public void setObjectVersionNumber(Long objectVersionNumber) {
//        this.objectVersionNumber = objectVersionNumber;
//    }
//
//    public Long getObjectVersionNumber() {
//        return objectVersionNumber;
//    }
//
//    public void setMapRank(String mapRank) {
//        this.mapRank = mapRank;
//    }
//
//    public String getMapRank() {
//        return mapRank;
//    }
//
//    public void setPriorityVO(PriorityVO priorityDTO) {
//        this.priorityDTO = priorityDTO;
//    }
//
//    public PriorityVO getPriorityVO() {
//        return priorityDTO;
//    }
//
//    public void setStatusId(Long statusId) {
//        this.statusId = statusId;
//    }
//
//    public Long getStatusId() {
//        return statusId;
//    }
//
//    public void setStatusMapVO(StatusMapVO statusMapVO) {
//        this.statusMapVO = statusMapVO;
//    }
//
//    public StatusMapVO getStatusMapVO() {
//        return statusMapVO;
//    }
//
//    public void setIssueTypeVO(IssueTypeVO issueTypeDTO) {
//        this.issueTypeDTO = issueTypeDTO;
//    }
//
//    public IssueTypeVO getIssueTypeVO() {
//        return issueTypeDTO;
//    }
//}
