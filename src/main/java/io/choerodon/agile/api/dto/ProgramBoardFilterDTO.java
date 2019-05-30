package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public class ProgramBoardFilterDTO {

    @ApiModelProperty(value = "只显示有依赖关系的公告板特性")
    private Boolean onlyDependFeature;

    public Boolean getOnlyDependFeature() {
        return onlyDependFeature;
    }

    public void setOnlyDependFeature(Boolean onlyDependFeature) {
        this.onlyDependFeature = onlyDependFeature;
    }
}

