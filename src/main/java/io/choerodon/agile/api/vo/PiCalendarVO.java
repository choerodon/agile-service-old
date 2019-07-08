package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/25.
 * Email: fuqianghuang01@gmail.com
 */
public class PiCalendarVO {

    @ApiModelProperty(value = "PI id")
    private Long id;

    @ApiModelProperty(value = "PI编码")
    private String code;

    @ApiModelProperty(value = "PI名称")
    private String name;

    @ApiModelProperty(value = "PI状态")
    private String statusCode;

    @ApiModelProperty(value = "PI开始时间")
    private Date startDate;

    @ApiModelProperty(value = "PI结束时间")
    private Date endDate;

    @ApiModelProperty(value = "PI所属的ART id")
    private Long artId;

    @ApiModelProperty(value = "项目群id")
    private Long programId;

    @ApiModelProperty(value = "PI下的冲刺信息")
    List<SprintCalendarVO> sprintCalendarVOList;

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

    public List<SprintCalendarVO> getSprintCalendarVOList() {
        return sprintCalendarVOList;
    }

    public void setSprintCalendarVOList(List<SprintCalendarVO> sprintCalendarVOList) {
        this.sprintCalendarVOList = sprintCalendarVOList;
    }
}
