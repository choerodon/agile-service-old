package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.validation.constraints.NotNull;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/30
 */
public class EpicSequenceDTO {


    private static final String EPIC_ID_NOT_NULL_ERROR = "error.epicId.NotNull";
    private static final String OBJECT_VERSION_NUMBER_NULL_ERROR = "error.objectVersionNumber.NotNull";
    private static final String BEFORE_SEQUENCE_NULL_ERROR = "error.beforeSequence.NotNull";
    private static final String AFTER_SEQUENCE_NULL_ERROR = "error.afterSequence.NotNull";


    @NotNull(message = EPIC_ID_NOT_NULL_ERROR)
    private Long epicId;

    @NotNull(message = OBJECT_VERSION_NUMBER_NULL_ERROR)
    private Long objectVersionNumber;

    @NotNull(message = BEFORE_SEQUENCE_NULL_ERROR)
    private Integer beforeSequence;

    @NotNull(message = AFTER_SEQUENCE_NULL_ERROR)
    private Integer afterSequence;

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
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

