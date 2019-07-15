package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.BoardVO;
import io.choerodon.core.exception.CommonException;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/13.
 * Email: fuqianghuang01@gmail.com
 */
public class BoardValidator {

    private static final String ERROR_PROJECTID_NOTEQUAL = "error.projectId.notEqual";
    private static final String ERROR_OBJECTVERSIONNUMBER_ISNULL = "error.objectVersionNumber.isNull";

    private BoardValidator() {}

    public static void checkUpdateBoard(Long projectId, BoardVO boardVO) {
        if (!projectId.equals(boardVO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
        if (boardVO.getObjectVersionNumber() == null) {
            throw new CommonException(ERROR_OBJECTVERSIONNUMBER_ISNULL);
        }
    }


}
