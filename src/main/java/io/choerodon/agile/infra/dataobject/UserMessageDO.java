package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
public class UserMessageDO {
    private String name;

    private String imageUrl;

    private String email;

    public UserMessageDO(){}

    public UserMessageDO(String name, String imageUrl,String email) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.email = email;
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
