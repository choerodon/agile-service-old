package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Creator: changpingshi0213@gmail.com
 * Date:  16:17 2018/8/28
 * Description: 未完成的任务DTO
 */
public class UnfinishedIssueDTO {

    @ApiModelProperty(value = "问题Id")
    private Long issueId;

    @ApiModelProperty(value = "问题编号")
    private String issueNum;

    @ApiModelProperty(value = "问题类型code")
    private String typeCode;

    @ApiModelProperty(value = "问题概要")
    private String summary;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityDTO priorityDTO;

    @ApiModelProperty(value = "问题类型DTO")
    private IssueTypeDTO issueTypeDTO;

    @ApiModelProperty(value = "状态DTO")
    private StatusMapVO statusMapVO;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
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

}
