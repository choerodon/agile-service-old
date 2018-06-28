package io.choerodon.agile.api.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/6/20.
 */
public class ProductVersionMergeDTO {
    private static final String SOURCE_VERSION_ERROR = "error.sourceVersionIds.notNull";
    private static final String TARGET_VERSION_ERROR = "error.targetVersionId.notNull";

    @NotNull(message = SOURCE_VERSION_ERROR)
    private List<Long> sourceVersionIds;
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
