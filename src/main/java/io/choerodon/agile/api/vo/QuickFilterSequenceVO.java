package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.validation.constraints.NotNull;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/14
 */
public class QuickFilterSequenceVO {

    private static final String FILTER_ID_NOT_NULL_ERROR = "error.filterId.NotNull";
    private static final String OBJECT_VERSION_NUMBER_NULL_ERROR = "error.objectVersionNumber.NotNull";

    @NotNull(message = FILTER_ID_NOT_NULL_ERROR)
    private Long filterId;

    @NotNull(message = OBJECT_VERSION_NUMBER_NULL_ERROR)
    private Long objectVersionNumber;

    private Integer beforeSequence;

    private Integer afterSequence;

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Integer getBeforeSequence() {
        return beforeSequence;
    }

    public void setBeforeSequence(Integer beforeSequence) {
        this.beforeSequence = beforeSequence;
    }

    public Integer getAfterSequence() {
        return afterSequence;
    }

    public void setAfterSequence(Integer afterSequence) {
        this.afterSequence = afterSequence;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}