package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintMapper extends Mapper<SprintDTO> {
    List<SprintNameDTO> queryNameByOptions(@Param("projectId") Long projectId, @Param("sprintStatusCodes") List<String> sprintStatusCodes);

    /**
     * 根据项目id和冲刺id查询冲刺
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @return SprintDTO
     */
    SprintDTO queryByProjectIdAndSprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    Boolean hasIssue(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    String queryMaxRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    SprintDTO getActiveSprint(@Param("projectId") Long projectId);

    int selectCountByStartedSprint(@Param("projectId") Long projectId);

    String queryMinRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<SprintSearchDTO> queryPlanSprint(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    int queryIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    BigDecimal queryStoryPoint(@Param("statusIds") List<Long> statusIds, @Param("issueIds") List<Long> issueIds, @Param("projectId") Long projectId);

    SprintDTO queryLastSprint(@Param("projectId") Long projectId);

    List<SprintNameDTO> queryPlanSprintName(@Param("projectId") Long projectId);

    int queryDoneIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    int queryNotDoneIssueCount(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<Long> queryIssueIdOrderByRankDesc(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<IssueNumDTO> queryParentsDoneUnfinishedSubtasks(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<Long> queryIssueIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<Long> queryAllRankIssueIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    Set<Long> queryAssigneeIdsByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<IssueSearchDTO> queryBacklogIssues(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    SprintSearchDTO queryActiveSprintNoIssueIds(@Param("projectId") Long projectId);

    List<SprintSearchDTO> queryPlanSprintNoIssueIds(@Param("projectId") Long projectId);

    List<SprintDTO> queryUnClosedSprint(Long projectId);

    /**
     * 查询issueId没有关闭的所属冲刺id
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return sprintId
     */
    Long queryNotCloseSprintIdByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

    /**
     * 查询不在计划中的冲刺
     *
     * @param projectId projectId
     * @return SprintDTO
     */
    List<SprintDTO> queryNotPlanSprintByProjectId(@Param("projectId") Long projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    Integer queryIssueCountInActiveBoard(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<Long> queryParentsDoneSubtaskUnDoneIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<Long> queryUnDoneSubOfParentIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    /**
     * 活跃冲刺的经办人统计信息
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @return AssigneeIssueDTO
     */
    List<AssigneeIssueDTO> queryAssigneeIssueByActiveSprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    /**
     * 查询活跃冲刺的issue列表
     *
     * @param sprintId sprintId
     * @param issueIds issueIds
     * @return IssueSearchDTO
     */
    List<IssueSearchDTO> queryActiveSprintIssueSearchByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("sprintId") Long sprintId);

    /**
     * 查询待办事项的所有冲刺中的所有用户
     *
     * @param projectId projectId
     * @return assigneeId
     */
    Set<Long> queryBacklogSprintAssigneeIds(@Param("projectId") Long projectId);

    List<Long> selectNotDoneByPiId(@Param("projectId") Long projectId, @Param("piId") Long piId);

    void updateSprintNameByBatch(@Param("projectId") Long projectId, @Param("sprintIds") List<Long> sprintIds);

    List<SprintDTO> selectListByPiId(@Param("projectId") Long projectId, @Param("piId") Long PiId);

    SprintDTO selectFirstSprintByPiId(@Param("projectId") Long projectId, @Param("piId") Long PiId);

    List<SprintDTO> getSprintByProjectId(@Param("projectId") Long projectId);

    List<SprintDTO> selectNotDoneByProjectId(@Param("projectId") Long projectId);
}
