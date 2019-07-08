package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public class PiDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "pi编码")
    private String code;

    @ApiModelProperty(value = "pi名称")
    private String name;

    @ApiModelProperty(value = "pi起始编号")
    private String seqNumber;

    @ApiModelProperty(value = "pi状态")
    private String statusCode;

    @ApiModelProperty(value = "pi开始时间")
    private Date startDate;

    @ApiModelProperty(value = "pi结束时间")
    private Date endDate;

    @ApiModelProperty(value = "art主键id")
    private Long artId;

    @ApiModelProperty(value = "项目群id")
    private Long programId;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "目标pi主键id，完成pi时将未完成的feature移动到目标pi时用")
    @Transient
    private Long targetPiId;

    @ApiModelProperty(value = "转换的目标状态")
    private Long updateStatusId;

    @ApiModelProperty(value = "转换目标状态的类别")
    private String statusCategoryCode;

    public void setUpdateStatusId(Long updateStatusId) {
        this.updateStatusId = updateStatusId;
    }

    public Long getUpdateStatusId() {
        return updateStatusId;
    }

    public void setStatusCategoryCode(String statusCategoryCode) {
        this.statusCategoryCode = statusCategoryCode;
    }

    public String getStatusCategoryCode() {
        return statusCategoryCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(String seqNumber) {
        this.seqNumber = seqNumber;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
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

    public Long getArtId() {
        return artId;
    }

    public void setArtId(Long artId) {
        this.artId = artId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setTargetPiId(Long targetPiId) {
        this.targetPiId = targetPiId;
    }

    public Long getTargetPiId() {
        return targetPiId;
    }
}
