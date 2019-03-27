package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/18.
 * Email: fuqianghuang01@gmail.com
 */
public class PiWithFeatureDTO {

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

    @ApiModelProperty(value = "pi下的feature列表")
    private List<SubFeatureDTO> subFeatureDTOList;

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

    public void setSubFeatureDTOList(List<SubFeatureDTO> subFeatureDTOList) {
        this.subFeatureDTOList = subFeatureDTOList;
    }

    public List<SubFeatureDTO> getSubFeatureDTOList() {
        return subFeatureDTOList;
    }
}

