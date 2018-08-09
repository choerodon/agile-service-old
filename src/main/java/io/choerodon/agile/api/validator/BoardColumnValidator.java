package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.dto.BoardColumnDTO;
import io.choerodon.core.exception.CommonException;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/9.
 * Email: fuqianghuang01@gmail.com
 */
public class BoardColumnValidator {

    public static void checkBoardColumnDTO(BoardColumnDTO boardColumnDTO) {
        if (boardColumnDTO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (boardColumnDTO.getBoardId() == null) {
            throw new CommonException("error.boardId.isNull");
        }
        if (boardColumnDTO.getCategoryCode() == null ) {
            throw new CommonException("error.categoryCode.isNull");
        }
    }

}
