package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2019/1/23
 */
public class QuickFilterSearchDTO {

    private String filterName;

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
