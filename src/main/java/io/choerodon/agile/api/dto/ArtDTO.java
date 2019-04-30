package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public class ArtDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "art编码")
    private String code;

    @ApiModelProperty(value = "art名称")
    private String name;

    @ApiModelProperty(value = "art描述")
    private String description;

    @ApiModelProperty(value = "art起始编号")
    private String seqNumber;

//    @ApiModelProperty(value = "是否启用，true表示启用，false表示未启用")
//    private Boolean enabled;

    @ApiModelProperty(value = "发布火车工程师用户id")
    private Long rteId;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "ip周数")
    private Long ipWeeks;

    @ApiModelProperty(value = "pi编码前缀")
    private String piCodePrefix;

    @ApiModelProperty(value = "art编码")
    private Long piCodeNumber;

    @ApiModelProperty(value = "迭代数量")
    private Long interationCount;

    @ApiModelProperty(value = "迭代周数")
    private Long interationWeeks;

    @ApiModelProperty(value = "项目群id")
    private Long programId;

    @ApiModelProperty(value = "pi数量")
    private Long piCount;

    @ApiModelProperty(value = "art状态code：todo、doing、done")
    private String statusCode;

    @ApiModelProperty(value = "数据库表版本号控制")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "发布火车工程师名称")
    @Transient
    private String rteName;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateDate;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(String seqNumber) {
        this.seqNumber = seqNumber;
    }

//    public Boolean getEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(Boolean enabled) {
//        this.enabled = enabled;
//    }

    public Long getRteId() {
        return rteId;
    }

    public void setRteId(Long rteId) {
        this.rteId = rteId;
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

    public void setIpWeeks(Long ipWeeks) {
        this.ipWeeks = ipWeeks;
    }

    public Long getIpWeeks() {
        return ipWeeks;
    }

    public String getPiCodePrefix() {
        return piCodePrefix;
    }

    public void setPiCodePrefix(String piCodePrefix) {
        this.piCodePrefix = piCodePrefix;
    }

    public Long getPiCodeNumber() {
        return piCodeNumber;
    }

    public void setPiCodeNumber(Long piCodeNumber) {
        this.piCodeNumber = piCodeNumber;
    }

    public Long getInterationCount() {
        return interationCount;
    }

    public void setInterationCount(Long interationCount) {
        this.interationCount = interationCount;
    }

    public void setInterationWeeks(Long interationWeeks) {
        this.interationWeeks = interationWeeks;
    }

    public Long getInterationWeeks() {
        return interationWeeks;
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

    public void setRteName(String rteName) {
        this.rteName = rteName;
    }

    public String getRteName() {
        return rteName;
    }

    public void setPiCount(Long piCount) {
        this.piCount = piCount;
    }

    public Long getPiCount() {
        return piCount;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }
}
