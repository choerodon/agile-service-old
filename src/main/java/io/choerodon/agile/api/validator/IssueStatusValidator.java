package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.dto.IssueStatusDTO;
import io.choerodon.core.exception.CommonException;

public class IssueStatusValidator {

    private IssueStatusValidator() {}

    public static void checkCreateStatus(Long projectId, IssueStatusDTO issueStatusDTO) {
        if (!projectId.equals(issueStatusDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
    }

    public static void checkUpdateStatus(Long projectId, IssueStatusDTO issueStatusDTO) {
        if (!projectId.equals(issueStatusDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        if (issueStatusDTO.getId() == null) {
            throw new CommonException("error.id.isNull");
        }
        if (issueStatusDTO.getCompleted() == null) {
            throw new CommonException("error.completed.isNull");
        }
    }
}
