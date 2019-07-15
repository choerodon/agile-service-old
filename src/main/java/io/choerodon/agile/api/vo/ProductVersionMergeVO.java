package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/6/20.
 */
public class ProductVersionMergeVO {
    private static final String SOURCE_VERSION_ERROR = "error.sourceVersionIds.notNull";
    private static final String TARGET_VERSION_ERROR = "error.targetVersionId.notNull";

    @ApiModelProperty(value = "将要被合并的版本id集合")
    @NotNull(message = SOURCE_VERSION_ERROR)
    private List<Long> sourceVersionIds;

    @ApiModelProperty(value = "目标版本id")
    @NotNull(message = TARGET_VERSION_ERROR)
    private Long targetVersionId;

    public List<Long> getSourceVersionIds() {
        return sourceVersionIds;
    }

    public void setSourceVersionIds(List<Long> sourceVersionIds) {
        this.sourceVersionIds = sourceVersionIds;
    }

    public Long getTargetVersionId() {
        return targetVersionId;
    }

    public void setTargetVersionId(Long targetVersionId) {
        this.targetVersionId = targetVersionId;
    }
}
