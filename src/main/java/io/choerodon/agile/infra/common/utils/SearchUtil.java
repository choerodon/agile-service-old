package io.choerodon.agile.infra.common.utils;


import java.util.Map;
import java.util.HashMap;

/**
 * @author dinghuang123@gmail.com
 */
public class SearchUtil {

    public static final String ADVANCE_SEARCH_ARGS = "advancedSearchArgs";
    public static final String SEARCH_ARGS = "searchArgs";

    private SearchUtil() {
    }

    public static Map<String, Object> setParam(Map<String, Object> searchParamMap) {
        Map<String, Object> result = new HashMap<>();
        //输入查询参数
        result.put(SEARCH_ARGS, StringUtil.cast(searchParamMap == null ? null : searchParamMap.get(SEARCH_ARGS)));
        //过滤查询参数
        result.put(ADVANCE_SEARCH_ARGS, StringUtil.cast(searchParamMap == null ? null : searchParamMap.get(ADVANCE_SEARCH_ARGS)));
        return result;
    }
}
