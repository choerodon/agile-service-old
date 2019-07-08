package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/7
 */
public class IssueComponentBriefDTO implements Serializable {

    @ApiModelProperty(value = "模块主键id")
    private Long componentId;

    @ApiModelProperty(value = "模块名称")
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
