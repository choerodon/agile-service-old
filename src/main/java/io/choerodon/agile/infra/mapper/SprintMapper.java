package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.dto.SprintUnClosedDTO;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    SprintSearchDO queryActiveSprint(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<SprintSearchDO> queryPlanSprint(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    int queryIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    int queryStoryPoint(@Param("categoryCode") String categoryCode, @Param("issueIds") List<Long> issueIds, @Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<IssueCountDO> queryIssueCountMap(@Param("projectId") Long projectId, @Param("sprintIds") List<Long> sprintIds);

    SprintDO queryLastSprint(@Param("projectId") Long projectId);

    List<SprintNameDO> queryPlanSprintName(@Param("projectId") Long projectId);

    int queryDoneIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    int queryNotDoneIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<AssigneeIssueDO> queryAssigneeIssueCount(@Param("sprintIds") List<Long> sprintIds, @Param("issueIds") List<Long> issueIds, @Param("assigned") Boolean assigned);

    List<AssigneeIssueDO> queryAssigneeIssueCountById(@Param("sprintId") Long sprintId, @Param("issueIds") List<Long> issueIds, @Param("assigned") Boolean assigned);

    List<Long> queryIssueIdOrderByRankDesc(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<IssueNumDO> queryParentsDoneUnfinishedSubtasks(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<Long> queryIssueIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    SprintNameDO querySprintNameBySprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<Long> queryAllRankIssueIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<Long> queryAssigneeIdsByIssueIds( @Param("issueIds")List<Long> issueIds);

    List<IssueSearchDO> queryBacklogIssues(@Param("projectId")Long projectId,@Param("issueIds")List<Long> issueIds);

    SprintSearchDO queryActiveSprintNoIssueIds(@Param("projectId")Long projectId);

    List<SprintSearchDO> queryPlanSprintNoIssueIds(@Param("projectId")Long projectId);

    List<SprintDO> queryUnClosedSprint(Long projectId);
}
