package io.choerodon.agile.infra.repository;

import io.choerodon.agile.api.dto.ColumnWithMaxMinNumDTO;
import io.choerodon.agile.domain.agile.entity.BoardColumnE;
import io.choerodon.agile.domain.agile.event.RemoveStatusWithProject;
import io.choerodon.agile.infra.dataobject.BoardColumnDO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardColumnRepository {

    BoardColumnE create(BoardColumnE boardColumnE);

    BoardColumnE update(BoardColumnE boardColumnE);

    void delete(Long cloumnId);

    void columnSort(Long projectId, Long boardId, BoardColumnE boardColumnE);

    void columnSortByProgram(Long projectId, Long boardId, BoardColumnE boardColumnE);

    BoardColumnE updateMaxAndMinNum(ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO);

    void updateSequenceWhenDelete(Long projectId, BoardColumnDO boardColumnDO);

    /**
     * 批量删除列和状态的关系
     *
     * @param removeStatusWithProjects removeStatusWithProjects
     */
    void batchDeleteColumnAndStatusRel(List<RemoveStatusWithProject> removeStatusWithProjects);
}
