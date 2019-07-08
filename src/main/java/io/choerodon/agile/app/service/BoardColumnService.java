package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.BoardColumnDTO;
import io.choerodon.agile.api.vo.ColumnSortDTO;
import io.choerodon.agile.api.vo.ColumnWithMaxMinNumDTO;
import io.choerodon.agile.domain.agile.entity.BoardE;
import io.choerodon.agile.api.vo.event.StatusPayload;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardColumnService {

    BoardColumnDTO create(Long projectId, String categoryCode, String applyType, BoardColumnDTO boardColumnDTO);

    BoardColumnDTO update(Long projectId, Long columnId, Long boardId, BoardColumnDTO boardColumnDTO);

    void delete(Long projectId, Long columnId);

    void deleteProgramBoardColumn(Long projectId, Long columnId);

    BoardColumnDTO queryBoardColumnById(Long projectId, Long columnId);

    void initBoardColumns(Long projectId, Long boardId, List<StatusPayload> statusPayloads);

    void columnSort(Long projectId, ColumnSortDTO columnSortDTO);

    void columnSortByProgram(Long projectId, ColumnSortDTO columnSortDTO);

    void createColumnWithRelateStatus(BoardE boardResult);

    BoardColumnDTO updateColumnContraint(Long projectId, Long columnId, ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO);

    void initBoardColumnsByProgram(Long projectId, Long boardId, List<StatusPayload> statusPayloads);

}
