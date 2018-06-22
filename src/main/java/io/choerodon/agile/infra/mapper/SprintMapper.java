package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintMapper extends BaseMapper<SprintDO> {
    List<SprintNameDO> queryNameByOptions(@Param("projectId") Long projectId, @Param("sprintStatusCodes") List<String> sprintStatusCodes);

    /**
     * 根据项目id和冲刺id查询冲刺
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @return SprintDO
     */
    SprintDO queryByProjectIdAndSprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    Boolean hasIssue(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    String queryMaxRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    SprintDO getActiveSprint(@Param("projectId") Long projectId);

    int selectCountByStartedSprint(@Param("projectId") Long projectId);

    String queryMinRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    SprintSearchDO queryActiveSprint(@Param("projectId") Long projectId);

    List<SprintSearchDO> queryPlanSprint(@Param("projectId") Long projectId);

    int queryIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    int queryStoryPoint(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("userId") Long userId, @Param("categoryCode") String categoryCode, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs);

    List<IssueCountDO> queryIssueCountMap(@Param("projectId") Long projectId, @Param("sprintIds") List<Long> sprintIds);

    SprintDO queryLastSprint(@Param("projectId") Long projectId);

    List<SprintNameDO> queryPlanSprintName(@Param("projectId") Long projectId);

    int queryDoneIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    int queryNotDoneIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<AssigneeIssueDO> queryAssigneeIssueCount(@Param("projectId") Long projectId, @Param("sprintIds") List<Long> sprintIds,  @Param("userId") Long userId, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs);

    List<AssigneeIssueDO> queryAssigneeIssueCountById(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId,  @Param("userId") Long userId, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs);

    List<Long> queryIssueIdOrderByRankDesc(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<IssueNumDO> queryParentsDoneUnfinishedSubtasks(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    String selectNameBySprintId(@Param("sprintId") Long sprintId);

    List<Long> queryIssueIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    SprintNameDO querySprintNameBySprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<SprintReportIssueSearchDO> queryReportSearchIssue(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("actualEndDate") Date actualEndDate, @Param("status") boolean status);

    List queryReportDoneIssues(@Param("projectId") Long projectId, @Param("reportSearchIssue") List<SprintReportIssueSearchDO> reportSearchIssue);
}
