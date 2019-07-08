package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public class PiObjectiveDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "pi目标名称")
    private String name;

    @ApiModelProperty(value = "计划商业价值")
    private Long planBv;

    @ApiModelProperty(value = "实际商业价值")
    private Long actualBv;

    @ApiModelProperty(value = "是否延伸")
    private Boolean stretch;

    @ApiModelProperty(value = "level编码")
    private String levelCode;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "pi主键id")
    private Long piId;

    @ApiModelProperty(value = "项目群id")
    private Long programId;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

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

    public Long getPlanBv() {
        return planBv;
    }

    public void setPlanBv(Long planBv) {
        this.planBv = planBv;
    }

    public Long getActualBv() {
        return actualBv;
    }

    public void setActualBv(Long actualBv) {
        this.actualBv = actualBv;
    }

    public Boolean getStretch() {
        return stretch;
    }

    public void setStretch(Boolean stretch) {
        this.stretch = stretch;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
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
}
