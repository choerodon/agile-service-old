package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/18.
 * Email: fuqianghuang01@gmail.com
 */
public class PiWithFeatureVO {

    @ApiModelProperty(value = "pi主键id")
    private Long id;

    @ApiModelProperty(value = "pi名称")
    private String name;

    @ApiModelProperty(value = "pi状态")
    private String statusCode;

    @ApiModelProperty(value = "pi开始时间")
    private Date startDate;

    @ApiModelProperty(value = "pi结束时间")
    private Date endDate;

    @ApiModelProperty(value = "pi所属的artId")
    private Long artId;

    @ApiModelProperty(value = "pi版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "pi下的feature列表")
    private List<SubFeatureVO> subFeatureVOList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setArtId(Long artId) {
        this.artId = artId;
    }

    public Long getArtId() {
        return artId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setSubFeatureVOList(List<SubFeatureVO> subFeatureVOList) {
        this.subFeatureVOList = subFeatureVOList;
    }

    public List<SubFeatureVO> getSubFeatureVOList() {
        return subFeatureVOList;
    }
}

