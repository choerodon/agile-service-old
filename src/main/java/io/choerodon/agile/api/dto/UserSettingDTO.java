package io.choerodon.agile.api.dto;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
public class UserSettingDTO {

    private Long settingId;

    private String swimlaneBasedCode;

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
