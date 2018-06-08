package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.BoardColumnDTO;
import io.choerodon.agile.api.dto.ColumnSortDTO;
import io.choerodon.agile.api.dto.ColumnWithMaxMinNumDTO;
import io.choerodon.agile.domain.agile.entity.BoardE;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardColumnService {

    BoardColumnDTO create(Long projectId, String categoryCode, BoardColumnDTO boardColumnDTO);

    BoardColumnDTO update(Long projectId, Long columnId, Long boardId, BoardColumnDTO boardColumnDTO);

    void delete(Long projectId, Long columnId);

    BoardColumnDTO queryBoardColumnById(Long projectId, Long columnId);

    void initBoardColumns(Long projectId, Long boardId);

    void columnSort(Long projectId, ColumnSortDTO columnSortDTO);

    void createColumnWithRelateStatus(BoardE boardResult);

    BoardColumnDTO updateColumnContraint(Long projectId, Long columnId, ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO);

    Boolean checkSameStatusName(Long projectId, String statusName);
}
