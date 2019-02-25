package io.choerodon.agile.api.dto;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchDTO {

    private PersonalFilterSearchArgsDTO searchArgs;
    private PersonalFilterSearchAdvancedArgsDTO advancedSearchArgs;

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

