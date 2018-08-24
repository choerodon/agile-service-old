package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.dto.StoryMapMoveDTO;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.core.exception.CommonException;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/9.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueValidator {

    private static final String ERROR_ISSUE_GET = "error.issue.get";
    private static final String ERROR_TYPECODE_ISSUBTASK = "error.typeCode.isSubtask";
    private static final String ERROR_PARENT_ISSUE_ISSUBTASK = "error.parentIssue.isSubtask";
    private static final String ERROR_PARENT_ISSUE_ISTEST = "error.parentIssue.isTest";
    private static final String ERROR_SPRINTIDANDVERSIONID_ALLNOTNULL = "error.sprintIdAndVersionId.allNotNull";
    private static final String ERROR_PARENT_ISSUE_NOT_EXIST = "error.parentIssue.get";

    private IssueValidator() {}

    public static void checkStoryMapMove(StoryMapMoveDTO storyMapMoveDTO) {
        if (storyMapMoveDTO.getSprintId() != null && storyMapMoveDTO.getVersionId() != null) {
            throw new CommonException(ERROR_SPRINTIDANDVERSIONID_ALLNOTNULL);
        }
    }

    public static void checkParentIdUpdate(IssueDO issueDO, IssueDO parentIssueDO) {
        if (issueDO == null) {
            throw new CommonException(ERROR_ISSUE_GET);
        }
        if (parentIssueDO == null) {
            throw new CommonException(ERROR_PARENT_ISSUE_NOT_EXIST);
        }
        String typeCode = issueDO.getTypeCode();
        if (!"sub_task".equals(typeCode)) {
            throw new CommonException(ERROR_TYPECODE_ISSUBTASK);
        }
        typeCode = parentIssueDO.getTypeCode();
        if ("sub_task".equals(typeCode)) {
            throw new CommonException(ERROR_PARENT_ISSUE_ISSUBTASK);
        }
        if ("issue_test".equals(typeCode)) {
            throw new CommonException(ERROR_PARENT_ISSUE_ISTEST);
        }
    }


}
