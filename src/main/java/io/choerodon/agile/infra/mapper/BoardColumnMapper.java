package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.ColumnWithMaxMinNumVO;
import io.choerodon.agile.api.vo.SearchDTO;
import io.choerodon.agile.api.vo.event.RemoveStatusWithProject;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardColumnMapper extends Mapper<BoardColumnDTO> {

    List selectColumnsByBoardId(@Param("projectId") Long projectId,
                                @Param("boardId") Long boardId,
                                @Param("activeSprintId") Long activeSprintId,
                                @Param("assigneeId") Long assigneeId,
                                @Param("onlyStory") Boolean onlyStory,
                                @Param("filterSql") String filterSql,
                                @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

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

    List queryColumnStatusRelByProjectId(@Param("projectId") Long projectId);

//    BoardColumnCheckDO selectColumnByColumnId(@Param("projectId") Long projectId,
//                                              @Param("columnId") Long columnId,
//                                              @Param("activeSprintId") Long activeSprintId);

//    BoardColumnCheckDO selectColumnByColumnIdWithoutSub(@Param("projectId") Long projectId,
//                                                        @Param("columnId") Long columnId,
//                                                        @Param("activeSprintId") Long activeSprintId);

    void updateMaxAndMinNum(@Param("columnInfo") ColumnWithMaxMinNumVO columnWithMaxMinNumVO);

    List<BoardColumnDTO> selectByBoardIdOrderBySequence(@Param("boardId") Long boardId);

    void updateSequenceWhenDelete(@Param("boardId") Long boardId, @Param("sequence") Integer sequence);

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

    List<EpicIdWithNameDO> selectEpicBatchByIds(@Param("epicIds") List<Long> epicIds);

    /**
     * 根据看板id和projectId查询列idList
     *
     * @param boardId   boardId
     * @param projectId projectId
     * @return Long
     */
    List<Long> queryColumnIdsByBoardId(@Param("boardId") Long boardId, @Param("projectId") Long projectId);

    /**
     * 批量删除列和状态的关系（包含状态）
     *
     * @param removeStatusWithProjects removeStatusWithProjects
     */
    void batchDeleteColumnAndStatusRel(@Param("removeStatusWithProjects") List<RemoveStatusWithProject> removeStatusWithProjects);

    List<Long> sortAndJudgeCompleted(@Param("projectId") Long projectId, @Param("parentIds") List<Long> parentIds);

    List<ParentIssueDO> queryParentIssuesByIds(@Param("projectId") Long projectId, @Param("parentIds") List<Long> parentIds);

    List<ColumnIssueNumDO> getAllColumnNum(@Param("projectId") Long projectId,
                                           @Param("boardId") Long boardId,
                                           @Param("activeSprintId") Long activeSprintId,
                                           @Param("columnConstraint") String columnConstraint);

    List<ColumnAndIssueDO> selectBoardByProgram(@Param("projectId") Long projectId,
                                                @Param("boardId") Long boardId,
                                                @Param("activePiId") Long activePiId,
                                                @Param("searchDTO")SearchDTO searchDTO);

    List<BoardColumnStatusRelDO> queryRelByColumnIds(@Param("columnIds") List<Long> columnIds);
}
