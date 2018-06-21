package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public interface IssueMapper extends BaseMapper<IssueDO> {

    int removeFromSprint(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    /**
     * 根据issueId查询issueDetail
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueDetailDO
     */
    IssueDetailDO queryIssueDetail(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    List<EpicDataDO> queryEpicList(@Param("projectId") Long projectId);

    List<IssueSearchDO> searchIssue(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs);

    int issueToDestination(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("targetSprintId") Long targetSprintId);

    Integer queryBacklogIssueCount(@Param("projectId") Long projectId);

    int batchIssueToVersion(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("issueIds") List<Long> issueIds);

    int batchIssueToEpic(@Param("projectId") Long projectId, @Param("epicId") Long epicId, @Param("issueIds") List<Long> issueIds);

    int batchIssueToSprint(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("moveIssues") List<MoveIssueDO> moveIssues);

    List<IssueSearchDO> queryIssueByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 根据项目id查询issue中的epic
     *
     * @param projectId projectId
     * @return IssueDO
     */
    List<IssueDO> queryIssueEpicSelectList(@Param("projectId") Long projectId);

    int batchRemoveFromVerion(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    String queryRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("outsetIssueId") Long outsetIssueId);

    List<Long> queryIssueIdOrderByRankDesc(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<Long> queryIssueIdOrderByRankAsc(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    String queryRightRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("leftRank") String leftRank);

    String queryLeftRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("rightRank") String rightRank);

    /**
     * 查询issue子任务列表
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueDO
     */
    List<IssueDO> queryIssueSubList(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    /**
     * 把issueId对应的epic下的issue的epicId置为0
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return int
     */
    int batchUpdateIssueEpicId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    List<IssueCountDO> queryIssueCountByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryDoneIssueCountByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryNotEstimateIssueCountByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryTotalEstimateByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    int subTaskToDestination(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("targetSprintId") Long targetSprintId);

    /**
     * 分页过滤查询issue列表，不包括子任务
     *
     * @param projectId          projectId
     * @param searchArgs         searchArgs
     * @param advancedSearchArgs advancedSearchArgs
     * @return IssueDO
     */
    List<Long> queryIssueListWithoutSub(@Param("projectId") Long projectId,
                                        @Param("searchArgs") Map<String, Object> searchArgs,
                                        @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs);

    /**
     * 批量更改子issue的冲刺id
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @param issueId   issueId
     * @return int
     */
    int batchUpdateSubIssueSprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("issueId") Long issueId);

    int batchSubIssueToSprint(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("issueIds") List<Long> issueIds);

    List<IssueLabelDO> selectLabelNameByIssueId(@Param("issueId") Long issueId);

    List<IssueCommonDO> listByOptions(@Param("projectId") Long projectId,
                                      @Param("typeCode") String typeCode);
}