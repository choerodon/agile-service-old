package io.choerodon.agile.api.validator;


import io.choerodon.agile.api.vo.WorkLogDTO;
import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.agile.infra.mapper.WorkLogMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/13.
 * Email: fuqianghuang01@gmail.com
 */

public class WorkLogValidator {

    private static final String ERROR_OBJECTVERSIONNUMBER_ISNULL = "error.objectVersionNumber.isNull";
    private static final String ERROR_LOGID_ISNULL = "error.logId.isNull";
    private static final String ERROR_PROJECTID_NOTNULL = "error.projectId.notEqual";
    private static final String ERROR_ISSUE_GET = "error.issue.get";

    @Autowired
    private static WorkLogMapper workLogMapper;

    private WorkLogValidator() {
    }

    public static void checkCreateWorkLog(Long projectId, WorkLogDTO workLogDTO, IssueDTO issueDTO) {
        if (!projectId.equals(workLogDTO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTNULL);
        }
        if (issueDTO == null) {
            throw new CommonException(ERROR_ISSUE_GET);
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
