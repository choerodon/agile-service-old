package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/6/4.
 */
public class ProductVersionReleaseDTO {
    private static final String PROJECT_ID_NULL_ERROR = "error.projectId.NotNull";
    private static final String VERSION_ID_NULL_ERROR = "error.versionId.NotNull";

    @ApiModelProperty(value = "项目id")
    @NotNull(message = PROJECT_ID_NULL_ERROR)
    private Long projectId;

    @ApiModelProperty(value = "当前版本id")
    @NotNull(message = VERSION_ID_NULL_ERROR)
    private Long versionId;

    @ApiModelProperty(value = "目标版本id")
    private Long targetVersionId;

    @ApiModelProperty(value = "发布时间")
    private Date releaseDate;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getTargetVersionId() {
        return targetVersionId;
    }

    public void setTargetVersionId(Long targetVersionId) {
        this.targetVersionId = targetVersionId;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
