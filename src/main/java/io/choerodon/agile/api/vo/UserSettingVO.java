package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
public class UserSettingVO {

    @ApiModelProperty(value = "设置主键id")
    private Long settingId;

    @ApiModelProperty(value = "用户泳道类型")
    private String swimlaneBasedCode;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    public Long getSettingId() {
        return settingId;
    }

    public void setSettingId(Long settingId) {
        this.settingId = settingId;
    }

    public String getSwimlaneBasedCode() {
        return swimlaneBasedCode;
    }

    public void setSwimlaneBasedCode(String swimlaneBasedCode) {
        this.swimlaneBasedCode = swimlaneBasedCode;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
