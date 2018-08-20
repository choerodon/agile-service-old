package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.dto.StoryMapMoveDTO;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.exception.CommonException;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/9.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueValidator {

    private static final String ERROR_EPICID_ISEQUAL = "error.epicId.isEqual";
    private static final String ERROR_ISSUE_GET = "error.issue.get";


    public static void checkStoryMapMove(StoryMapMoveDTO storyMapMoveDTO, IssueDO issueDO) {
        if (issueDO == null) {
            throw new CommonException(ERROR_ISSUE_GET);
        }
        Long originEpicId = storyMapMoveDTO.getOriginEpicId();
        Long epicId = storyMapMoveDTO.getEpicId();
        if (originEpicId.equals(epicId)) {
            throw new CommonException(ERROR_EPICID_ISEQUAL);
        }

    }

}
