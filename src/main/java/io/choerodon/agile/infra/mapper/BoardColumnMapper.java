package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.dto.ColumnWithMaxMinNumDTO;
import io.choerodon.agile.infra.dataobject.BoardColumnCheckDO;
import io.choerodon.agile.infra.dataobject.ColumnAndIssueDO;
import io.choerodon.agile.infra.dataobject.ColumnDO;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.BoardColumnDO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardColumnMapper extends BaseMapper<BoardColumnDO> {

    List selectColumnsByBoardId(@Param("projectId") Long projectId,
                                @Param("boardId") Long boardId,
                                @Param("activeSprintId") Long activeSprintId,
                                @Param("assigneeId") Long assigneeId,
                                @Param("onlyStory") Boolean onlyStory,
                                @Param("filterSql") String filterSql);

    void columnSort(@Param("boardId") Long boardId,
                    @Param("sequence") Integer sequence,
                    @Param("originSequence") Integer originSequence);

    void columnSortDesc(@Param("boardId") Long boardId,
                        @Param("sequence") Integer sequence,
                        @Param("originSequence") Integer originSequence);

    void updateColumnCategory(@Param("boardId") Long boardId,
                              @Param("columnNum") Integer columnNum);

    void updateColumnColor(@Param("boardId") Long boardId,
                           @Param("columnNum") Integer columnNum);

    BoardColumnCheckDO selectColumnByStatusId(@Param("projectId") Long projectId,
                                              @Param("statusId") Long statusId,
                                              @Param("columnId") Long columnId);

    BoardColumnCheckDO selectColumnByStatusIdWithoutSubIssue(@Param("projectId") Long projectId,
                                                             @Param("statusId") Long statusId,
                                                             @Param("columnId") Long columnId);

    List queryColumnStatusRelByProjectId(@Param("projectId") Long projectId);

    BoardColumnCheckDO selectColumnByColumnId(@Param("projectId") Long projectId,
                                              @Param("columnId") Long columnId,
                                              @Param("activeSprintId") Long activeSprintId);

    BoardColumnCheckDO selectColumnByColumnIdWithSubIssue(@Param("projectId") Long projectId,
                                                          @Param("columnId") Long columnId,
                                                          @Param("activeSprintId") Long activeSprintId);

    void updateMaxAndMinNum(@Param("columnInfo") ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO);

    List<BoardColumnDO> selectByBoardIdOrderBySequence(@Param("boardId") Long boardId);

    void updateSequenceWhenDelete(@Param("boardId") Long boardId, @Param("sequence") Integer sequence);

    /**
     * 根据冲刺id查询当前冲刺所有用户
     *
     * @param projectId      projectId
     * @param activeSprintId activeSprintId
     * @return Long
     */
    List<Long> queryAssigneeIdsBySprintId(@Param("projectId") Long projectId, @Param("activeSprintId") Long activeSprintId);

    /**
     * 根据列id获取列对象
     *
     * @param columnIds columnIds
     * @return ColumnDO
     */
    List<ColumnDO> queryColumnByColumnIds(@Param("columnIds") List<Long> columnIds);

    /**
     * 根据issueIds集合获取ColumnAndIssueDO
     *
     * @param issueIds issueIds
     * @param boardId  boardId
     * @return ColumnAndIssueDO
     */
    List<ColumnAndIssueDO> queryColumnsByIssueIds(@Param("issueIds") List<Long> issueIds, @Param("boardId") Long boardId);
}
