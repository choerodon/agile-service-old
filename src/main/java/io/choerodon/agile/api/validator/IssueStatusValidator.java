package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.IssueStatusVO;
import io.choerodon.core.exception.CommonException;

public class IssueStatusValidator {

    private IssueStatusValidator() {}

    public static void checkCreateStatus(Long projectId, IssueStatusVO issueStatusVO) {
        if (!projectId.equals(issueStatusVO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
    }

    public static void checkUpdateStatus(Long projectId, IssueStatusVO issueStatusVO) {
        if (!projectId.equals(issueStatusVO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        if (issueStatusVO.getId() == null) {
            throw new CommonException("error.id.isNull");
        }
        if (issueStatusVO.getCompleted() == null) {
            throw new CommonException("error.completed.isNull");
        }
    }
}
