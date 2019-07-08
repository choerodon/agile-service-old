package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.BoardColumnDTO;
import io.choerodon.agile.api.vo.ColumnSortDTO;
import io.choerodon.agile.api.vo.ColumnWithMaxMinNumDTO;
import io.choerodon.agile.infra.dataobject.BoardColumnDO;
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

    public static void checkCreateBoardColumnDTO(Long projectId, BoardColumnDTO boardColumnDTO) {
        if (!projectId.equals(boardColumnDTO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
    }

    public static void checkUpdateBoardColumnDTO(Long projectId, Long boardId, BoardColumnDTO boardColumnDTO) {
        if (!projectId.equals(boardColumnDTO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
        if (!boardId.equals(boardColumnDTO.getBoardId())) {
            throw new CommonException(ERROR_BOARDID_NOTEQUAL);
        }
    }

    public static void checkDeleteColumn(BoardColumnDO boardColumnDO) {
        if (boardColumnDO == null) {
            throw new CommonException(ERROR_COLUMN_ISNULL);
        }
    }

    public static void checkUpdateColumnContraint(Long projectId, Long columnId, ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO) {
        if (!projectId.equals(columnWithMaxMinNumDTO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
        if (!columnId.equals(columnWithMaxMinNumDTO.getColumnId())) {
            throw new CommonException(ERROR_COLUMNID_NOTEQUAL);
        }
        if (columnWithMaxMinNumDTO.getMaxNum() != null && columnWithMaxMinNumDTO.getMinNum() != null && columnWithMaxMinNumDTO.getMinNum() > columnWithMaxMinNumDTO.getMaxNum()) {
            throw new CommonException(ERROR_NUM_MINNUMCANNOTUPTOMAXNUM);
        }
    }

    public static void checkColumnSort(Long projectId, ColumnSortDTO columnSortDTO) {

        if (!projectId.equals(columnSortDTO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
    }
}
