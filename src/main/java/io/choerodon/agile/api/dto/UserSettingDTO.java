package io.choerodon.agile.api.dto;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
public class UserSettingDTO {

    private Long settingId;

    private Long defaultBoardId;

    private Long objectVersionNumber;

    public Long getSettingId() {
        return settingId;
    }

    public void setSettingId(Long settingId) {
        this.settingId = settingId;
    }

    public Long getDefaultBoardId() {
        return defaultBoardId;
    }

    public void setDefaultBoardId(Long defaultBoardId) {
        this.defaultBoardId = defaultBoardId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
