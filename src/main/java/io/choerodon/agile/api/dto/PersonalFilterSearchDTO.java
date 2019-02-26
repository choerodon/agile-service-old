package io.choerodon.agile.api.dto;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchDTO {

    /**
     * 搜索参数
     */
    private PersonalFilterSearchArgsDTO searchArgs;
    /**
     * 高级搜索，issue的必填字段的对应id值，在sql中用in匹配
     */
    private PersonalFilterSearchAdvancedArgsDTO advancedSearchArgs;
    /**
     * 其他搜索，issue的非必填字段的对应id值，在sql中用in匹配
     */
    private PersonalFilterSearchOtherArgsDTO otherArgs;

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

