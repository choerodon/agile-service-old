package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.BoardColumnVO;
import io.choerodon.agile.api.vo.ColumnSortVO;
import io.choerodon.agile.api.vo.ColumnWithMaxMinNumVO;
import io.choerodon.agile.api.vo.event.RemoveStatusWithProject;
import io.choerodon.agile.domain.agile.entity.BoardE;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.infra.dataobject.BoardColumnDTO;
import io.choerodon.agile.infra.dataobject.BoardDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardColumnService {

    BoardColumnVO create(Long projectId, String categoryCode, String applyType, BoardColumnVO boardColumnVO);

    BoardColumnVO update(Long projectId, Long columnId, Long boardId, BoardColumnVO boardColumnVO);

    void delete(Long projectId, Long columnId);

    void deleteProgramBoardColumn(Long projectId, Long columnId);

    BoardColumnVO queryBoardColumnById(Long projectId, Long columnId);

    void initBoardColumns(Long projectId, Long boardId, List<StatusPayload> statusPayloads);

    void columnSort(Long projectId, ColumnSortVO columnSortVO);

    void columnSortByProgram(Long projectId, ColumnSortVO columnSortVO);

    void createColumnWithRelateStatus(BoardDTO boardResult);

    BoardColumnVO updateColumnContraint(Long projectId, Long columnId, ColumnWithMaxMinNumVO columnWithMaxMinNumVO);

    void initBoardColumnsByProgram(Long projectId, Long boardId, List<StatusPayload> statusPayloads);

    BoardColumnDTO createBase(BoardColumnDTO boardColumnDTO);

    void batchDeleteColumnAndStatusRel(List<RemoveStatusWithProject> removeStatusWithProjects);

}
