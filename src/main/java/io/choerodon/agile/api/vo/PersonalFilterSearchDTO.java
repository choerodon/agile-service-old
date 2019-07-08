package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchDTO {

    /**
     * 搜索参数
     */
    @ApiModelProperty(value = "搜索参数")
    private PersonalFilterSearchArgsDTO searchArgs;
    /**
     * 高级搜索，issue的必填字段的对应id值，在sql中用in匹配
     */
    @ApiModelProperty(value = "高级搜索，issue的必填字段的对应id值，在sql中用in匹配")
    private PersonalFilterSearchAdvancedArgsDTO advancedSearchArgs;
    /**
     * 其他搜索，issue的非必填字段的对应id值，在sql中用in匹配
     */
    @ApiModelProperty(value = "其他搜索，issue的非必填字段的对应id值，在sql中用in匹配")
    private PersonalFilterSearchOtherArgsDTO otherArgs;

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

    public PersonalFilterSearchOtherArgsDTO getOtherArgs() {
        return otherArgs;
    }

    public void setOtherArgs(PersonalFilterSearchOtherArgsDTO otherArgs) {
        this.otherArgs = otherArgs;
    }

    public PersonalFilterSearchArgsDTO getSearchArgs() {
        return searchArgs;
    }

    public void setSearchArgs(PersonalFilterSearchArgsDTO searchArgs) {
        this.searchArgs = searchArgs;
    }

    public PersonalFilterSearchAdvancedArgsDTO getAdvancedSearchArgs() {
        return advancedSearchArgs;
    }

    public void setAdvancedSearchArgs(PersonalFilterSearchAdvancedArgsDTO advancedSearchArgs) {
        this.advancedSearchArgs = advancedSearchArgs;
    }
}

