package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
public class UserMessageDTO {
    private String name;

    private String loginName;

    private String realName;

    private String imageUrl;

    private String email;

    public UserMessageDTO(String name, String imageUrl, String email) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public UserMessageDTO(String name, String loginName, String realName, String imageUrl, String email) {
        this.name = name;
        this.loginName = loginName;
        this.realName = realName;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public String getLoginName() {

        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
