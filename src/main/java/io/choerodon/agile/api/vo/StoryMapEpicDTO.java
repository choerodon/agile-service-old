//package io.choerodon.agile.api.vo;
//
//import io.swagger.annotations.ApiModelProperty;
//
//import java.math.BigDecimal;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/8/10.
// * Email: fuqianghuang01@gmail.com
// */
//public class StoryMapEpicDTO {
//
//    @ApiModelProperty(value = "问题主键id")
//    private Long issueId;
//
//    @ApiModelProperty(value = "问题概要")
//    private String summary;
//
//    @ApiModelProperty(value = "描述")
//    private String description;
//
//    @ApiModelProperty(value = "史诗名称")
//    private String epicName;
//
//    @ApiModelProperty(value = "项目id")
//    private Long projectId;
//
//    @ApiModelProperty(value = "问题数量")
//    private Integer issueCount;
//
//    @ApiModelProperty(value = "已完成的问题数量")
//    private Integer doneIssueCount;
//
//    @ApiModelProperty(value = "未预估的故事数量")
//    private Integer notEstimate;
//
//    @ApiModelProperty(value = "总的预估故事点")
//    private BigDecimal totalEstimate;
//
//    @ApiModelProperty(value = "史诗颜色")
//    private String color;
//
//    @ApiModelProperty(value = "版本号")
//    private Long objectVersionNumber;
//
//    @ApiModelProperty(value = "史诗排序字段")
//    private Integer epicSequence;
//
//    @ApiModelProperty(value = "状态id")
//    private Long statusId;
//
//    @ApiModelProperty(value = "状态DTO")
//    private StatusMapVO statusMapVO;
//
//    @ApiModelProperty(value = "问题类型id")
//    private Long issueTypeId;
//
//    @ApiModelProperty(value = "问题类型DTO")
//    private IssueTypeVO issueTypeDTO;
//
//    @ApiModelProperty(value = "问题编号")
//    private String issueNum;
//
//    public Integer getEpicSequence() {
//        return epicSequence;
//    }
//
//    public void setEpicSequence(Integer epicSequence) {
//        this.epicSequence = epicSequence;
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
//    public String getSummary() {
//        return summary;
//    }
//
//    public void setSummary(String summary) {
//        this.summary = summary;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public Long getProjectId() {
//        return projectId;
//    }
//
//    public void setProjectId(Long projectId) {
//        this.projectId = projectId;
//    }
//
//    public Integer getIssueCount() {
//        return issueCount;
//    }
//
//    public void setIssueCount(Integer issueCount) {
//        this.issueCount = issueCount;
//    }
//
//    public String getColor() {
//        return color;
//    }
//
//    public void setColor(String color) {
//        this.color = color;
//    }
//
//    public Integer getDoneIssueCount() {
//        return doneIssueCount;
//    }
//
//    public void setDoneIssueCount(Integer doneIssueCount) {
//        this.doneIssueCount = doneIssueCount;
//    }
//
//    public Integer getNotEstimate() {
//        return notEstimate;
//    }
//
//    public void setNotEstimate(Integer notEstimate) {
//        this.notEstimate = notEstimate;
//    }
//
//    public String getEpicName() {
//        return epicName;
//    }
//
//    public void setEpicName(String epicName) {
//        this.epicName = epicName;
//    }
//
//    public Long getObjectVersionNumber() {
//        return objectVersionNumber;
//    }
//
//    public void setObjectVersionNumber(Long objectVersionNumber) {
//        this.objectVersionNumber = objectVersionNumber;
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
//    public void setIssueTypeId(Long issueTypeId) {
//        this.issueTypeId = issueTypeId;
//    }
//
//    public Long getIssueTypeId() {
//        return issueTypeId;
//    }
//
//    public void setIssueTypeVO(IssueTypeVO issueTypeDTO) {
//        this.issueTypeDTO = issueTypeDTO;
//    }
//
//    public IssueTypeVO getIssueTypeVO() {
//        return issueTypeDTO;
//    }
//
//    public void setTotalEstimate(BigDecimal totalEstimate) {
//        this.totalEstimate = totalEstimate;
//    }
//
//    public BigDecimal getTotalEstimate() {
//        return totalEstimate;
//    }
//}
