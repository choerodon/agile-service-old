package io.choerodon.agile.infra.common.utils;

import io.choerodon.core.exception.CommonException;

import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/6/7.
 */
public class ColorUtil {
    private static final String ISSUE_STATUS_CODE_TODO = "todo";
    private static final String ISSUE_STATUS_CODE_DOING = "doing";
    private static final String ISSUE_STATUS_CODE_DONE = "done";
    private static final String ISSUE_STATUS_COLOR_TODO = "issue_status_color_todo";
    private static final String ISSUE_STATUS_COLOR_DOING = "issue_status_color_doing";
    private static final String ISSUE_STATUS_COLOR_DONE = "issue_status_color_done";
    private static final String STATUS_CODE_NULL_ERROR = "error.issueAssembler.statusCodeNull";

    private ColorUtil() {
    }

    public static String initializationStatusColor(String statusCode, Map<String, String> lookupValueMap) {
        if (statusCode == null) {
            return null;
        } else {
            switch (statusCode) {
                case ISSUE_STATUS_CODE_TODO:
                    return lookupValueMap.get(ISSUE_STATUS_COLOR_TODO);
                case ISSUE_STATUS_CODE_DOING:
                    return lookupValueMap.get(ISSUE_STATUS_COLOR_DOING);
                case ISSUE_STATUS_CODE_DONE:
                    return lookupValueMap.get(ISSUE_STATUS_COLOR_DONE);
                default:
                    throw new CommonException(STATUS_CODE_NULL_ERROR);
            }
        }
    }
}
