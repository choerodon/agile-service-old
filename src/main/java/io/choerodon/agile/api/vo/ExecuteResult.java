package io.choerodon.agile.api.vo;


import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @date 2018/9/19
 */
public class ExecuteResult {
    @ApiModelProperty(value = "是否执行成功")
    Boolean isSuccess;
    @ApiModelProperty(value = "结果状态id")
    Long resultStatusId;
    @ApiModelProperty(value = "错误信息")
    String errorMessage;

    public ExecuteResult() {
    }

    public ExecuteResult(Boolean isSuccess, Long resultStatusId, String errorMessage) {
        this.isSuccess = isSuccess;
        this.resultStatusId = resultStatusId;
        this.errorMessage = errorMessage;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Long getResultStatusId() {
        return resultStatusId;
    }

    public void setResultStatusId(Long resultStatusId) {
        this.resultStatusId = resultStatusId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
