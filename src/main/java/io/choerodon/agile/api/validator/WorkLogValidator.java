package io.choerodon.agile.api.validator;


import io.choerodon.agile.api.dto.WorkLogDTO;
import io.choerodon.core.exception.CommonException;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/13.
 * Email: fuqianghuang01@gmail.com
 */

public class WorkLogValidator {

    private static final String ERROR_OBJECTVERSIONNUMBER_ISNULL = "error.objectVersionNumber.isNull";
    private static final String ERROR_LOGID_ISNULL = "error.logId.isNull";
    private static final String ERROR_PROJECTID_NOTNULL = "error.projectId.notEqual";

    public static void checkCreateWorkLog(Long projectId, WorkLogDTO workLogDTO) {
        if (!projectId.equals(workLogDTO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTNULL);
        }
    }

    public static void checkUpdateWorkLog(WorkLogDTO workLogDTO) {
        if (workLogDTO.getLogId() == null) {
            throw new CommonException(ERROR_LOGID_ISNULL);
        }
        if (workLogDTO.getObjectVersionNumber() == null) {
            throw new CommonException(ERROR_OBJECTVERSIONNUMBER_ISNULL);
        }
    }
}
