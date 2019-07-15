package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.BoardColumnVO;
import io.choerodon.agile.api.vo.ColumnSortVO;
import io.choerodon.agile.api.vo.ColumnWithMaxMinNumVO;
import io.choerodon.agile.infra.dataobject.BoardColumnDTO;
import io.choerodon.core.exception.CommonException;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/9.
 * Email: fuqianghuang01@gmail.com
 */
public class BoardColumnValidator {


    private static final String ERROR_PROJECTID_NOTEQUAL = "error.projectId.notEqual";
    private static final String ERROR_BOARDID_NOTEQUAL = "error.boardId.notEqual";
    private static final String ERROR_COLUMNID_NOTEQUAL = "error.columnId.notEqual";
    private static final String ERROR_NUM_MINNUMCANNOTUPTOMAXNUM = "error.num.minNumCannotUpToMaxNum";
    private static final String ERROR_COLUMN_ISNULL = "error.column.isNull";


    private BoardColumnValidator() {}

    public static void checkCreateBoardColumnDTO(Long projectId, BoardColumnVO boardColumnVO) {
        if (!projectId.equals(boardColumnVO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
    }

    public static void checkUpdateBoardColumnDTO(Long projectId, Long boardId, BoardColumnVO boardColumnVO) {
        if (!projectId.equals(boardColumnVO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
        if (!boardId.equals(boardColumnVO.getBoardId())) {
            throw new CommonException(ERROR_BOARDID_NOTEQUAL);
        }
    }

    public static void checkDeleteColumn(BoardColumnDTO boardColumnDTO) {
        if (boardColumnDTO == null) {
            throw new CommonException(ERROR_COLUMN_ISNULL);
        }
    }

    public static void checkUpdateColumnContraint(Long projectId, Long columnId, ColumnWithMaxMinNumVO columnWithMaxMinNumVO) {
        if (!projectId.equals(columnWithMaxMinNumVO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
        if (!columnId.equals(columnWithMaxMinNumVO.getColumnId())) {
            throw new CommonException(ERROR_COLUMNID_NOTEQUAL);
        }
        if (columnWithMaxMinNumVO.getMaxNum() != null && columnWithMaxMinNumVO.getMinNum() != null && columnWithMaxMinNumVO.getMinNum() > columnWithMaxMinNumVO.getMaxNum()) {
            throw new CommonException(ERROR_NUM_MINNUMCANNOTUPTOMAXNUM);
        }
    }

    public static void checkColumnSort(Long projectId, ColumnSortVO columnSortVO) {

        if (!projectId.equals(columnSortVO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
    }
}
