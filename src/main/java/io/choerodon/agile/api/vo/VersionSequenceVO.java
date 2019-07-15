package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/26
 */
public class VersionSequenceVO {


    private static final String VERSION_ID_NOT_NULL_ERROR = "error.versionId.NotNull";
    private static final String OBJECT_VERSION_NUMBER_NULL_ERROR = "error.objectVersionNumber.NotNull";


    @ApiModelProperty(value = "版本主键id")
    @NotNull(message = VERSION_ID_NOT_NULL_ERROR)
    private Long versionId;

    @ApiModelProperty(value = "版本号")
    @NotNull(message = OBJECT_VERSION_NUMBER_NULL_ERROR)
    private Long objectVersionNumber;

    @ApiModelProperty(value = "排在前面的序号")
    private Integer beforeSequence;

    @ApiModelProperty(value = "排在后面的序号")
    private Integer afterSequence;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
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
