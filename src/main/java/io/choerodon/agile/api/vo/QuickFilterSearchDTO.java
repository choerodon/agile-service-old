package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2019/1/23
 */
public class QuickFilterSearchDTO {

    @ApiModelProperty(value = "搜索名称")
    private String filterName;

    @ApiModelProperty(value = "搜索内容")
    private List<String> contents;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
