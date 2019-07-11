package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchVO {

    /**
     * 搜索参数
     */
    @ApiModelProperty(value = "搜索参数")
    private PersonalFilterSearchArgsVO searchArgs;
    /**
     * 高级搜索，issue的必填字段的对应id值，在sql中用in匹配
     */
    @ApiModelProperty(value = "高级搜索，issue的必填字段的对应id值，在sql中用in匹配")
    private PersonalFilterSearchAdvancedArgsVO advancedSearchArgs;
    /**
     * 其他搜索，issue的非必填字段的对应id值，在sql中用in匹配
     */
    @ApiModelProperty(value = "其他搜索，issue的非必填字段的对应id值，在sql中用in匹配")
    private PersonalFilterSearchOtherArgsVO otherArgs;

    /**
     * 全局搜索：目前仅包括issueNum和summery
     */
    @ApiModelProperty(value = "全局搜索：目前仅包括issueNum和summery")
    private List<String> contents;

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public PersonalFilterSearchOtherArgsVO getOtherArgs() {
        return otherArgs;
    }

    public void setOtherArgs(PersonalFilterSearchOtherArgsVO otherArgs) {
        this.otherArgs = otherArgs;
    }

    public PersonalFilterSearchArgsVO getSearchArgs() {
        return searchArgs;
    }

    public void setSearchArgs(PersonalFilterSearchArgsVO searchArgs) {
        this.searchArgs = searchArgs;
    }

    public PersonalFilterSearchAdvancedArgsVO getAdvancedSearchArgs() {
        return advancedSearchArgs;
    }

    public void setAdvancedSearchArgs(PersonalFilterSearchAdvancedArgsVO advancedSearchArgs) {
        this.advancedSearchArgs = advancedSearchArgs;
    }
}

