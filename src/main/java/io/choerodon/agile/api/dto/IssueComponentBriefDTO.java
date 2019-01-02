package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.io.Serializable;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/7
 */
public class IssueComponentBriefDTO implements Serializable {

    private Long componentId;

    private String name;


    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
