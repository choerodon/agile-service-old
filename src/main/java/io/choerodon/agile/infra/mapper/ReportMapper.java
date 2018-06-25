package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.ReportIssueDO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.dataobject.SprintReportIssueStatusDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public interface ReportMapper {

    /**
     * 获取当前冲刺开启前的issue的指定修改字段信息
     *
     * @param issueIdList issueIdList当前冲刺开启前的issueIdList
     * @param sprintDO    sprintDO
     * @param field       filed修改字段
     * @return ReportIssueDO
     */
    List<ReportIssueDO> queryValueBeforeSprintStart(@Param("issueIdList") List<Long> issueIdList, @Param("sprintDO") SprintDO sprintDO, @Param("field") String field);

    /**
     * 获取当前冲刺期间加入的issue(包含加入时间、加入时的字段值)
     *
     * @param issueIdAddList issueIdList当前冲刺期间加入的issueIdList
     * @param sprintDO       sprintDO
     * @param field          filed修改字段
     * @return ReportIssueE
     */
    List<ReportIssueDO> queryAddIssueValueDuringSprint(@Param("issueIdAddList") List<Long> issueIdAddList, @Param("sprintDO") SprintDO sprintDO, @Param("field") String field);

    /**
     * 获取当前冲刺期间移除的issue(包含移除时间、移除时的字段值)
     *
     * @param issueIdRemoveList issueIdRemoveList
     * @param sprintDO          sprintDO
     * @param field             filed修改字段
     * @return ReportIssueE
     */
    List<ReportIssueDO> queryRemoveIssueValueDurationSprint(@Param("issueIdRemoveList") List<Long> issueIdRemoveList, @Param("sprintDO") SprintDO sprintDO, @Param("field") String field);

    /**
     * 获取冲刺开启前的issue计数
     *
     * @param sprintE sprintE
     * @return issueCounts
     */
    Integer queryIssueTotalCountBeforeSprintStart(@Param("sprintE") SprintE sprintE);

    /**
     * 获取冲刺开启前的剩余预计时间总和
     *
     * @param issueIdList issueIdList
     * @param sprintDO    sprintDO
     * @return Integer
     */
    Integer queryRemainingTimesBeforeSprintStart(@Param("issueIdList") List<Long> issueIdList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 获取冲刺开启前的issue
     *
     * @param sprintDO sprintDO
     * @return issueIds
     */
    List<Long> queryIssueIdsBeforeSprintStart(@Param("sprintDO") SprintDO sprintDO);

    /**
     * 获取冲刺期间加入的issue
     *
     * @param sprintDO                sprintDO
     * @param issueIdBeforeSprintList issueIdBeforeSprintList
     * @return issueIdList
     */
    List<Long> queryAddIssueIdsDuringSprint(@Param("sprintDO") SprintDO sprintDO, @Param("issueIdBeforeSprintList") List<Long> issueIdBeforeSprintList);

    /**
     * 获取冲刺期间移除的issue
     *
     * @param sprintDO sprintDO
     * @return issueIdList
     */
    List<Long> queryRemoveIssueIdsDuringSprint(@Param("sprintDO") SprintDO sprintDO);

    /**
     * 获取冲刺关系中的的issue
     *
     * @param sprintId sprintId
     * @return issueIdList
     */
    List<Long> queryIssueIdsBySprintId(@Param("sprintId") Long sprintId);

    /**
     * 查询在冲刺期间添加的issue，包含issue加入的时间
     *
     * @param issueIdAddList issueIdAddList
     * @param sprintDO       sprintDO
     * @return ReportIssueDOList
     */
    List<ReportIssueDO> queryAddIssueDuringSprint(@Param("issueIdAddList") List<Long> issueIdAddList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 查询在冲刺期间移除的issue，包含issue移除的时间
     *
     * @param issueIdRemoveList issueIdRemoveList
     * @param sprintDO          sprintDO
     * @return ReportIssueDOList
     */
    List<ReportIssueDO> queryRemoveIssueDuringSprint(@Param("issueIdRemoveList") List<Long> issueIdRemoveList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 获取冲刺期间issue状态更改为done的issue
     *
     * @param sprintDO     sprintDO
     * @param issueAllList issueAllList
     * @return issueIds
     */
    List<Long> queryAddDoneIssueIdsDuringSprint(@Param("sprintDO") SprintDO sprintDO, @Param("issueAllList") List<Long> issueAllList);

    /**
     * 获取冲刺期间issue状态从done更改到其他的issue
     *
     * @param sprintDO     sprintDO
     * @param issueAllList issueAllList
     * @return issueIds
     */
    List<Long> queryRemoveDoneIssueIdsDuringSprint(@Param("sprintDO") SprintDO sprintDO, @Param("issueAllList") List<Long> issueAllList);

    /**
     * 获取冲刺期间移动到done状态的字段变更值（包含变更时间）
     *
     * @param issueIdAddDoneList issueIdAddDoneList
     * @param sprintDO           sprintDO
     * @param field              field
     * @return ReportIssueDOList
     */
    List<ReportIssueDO> queryAddIssueDoneValueDuringSprint(@Param("issueIdAddDoneList") List<Long> issueIdAddDoneList, @Param("sprintDO") SprintDO sprintDO, @Param("field") String field);

    /**
     * 获取冲刺期间done移动到非done状态的字段变更值（包含变更时间）
     *
     * @param issueIdRemoveDoneList issueIdRemoveDoneList
     * @param sprintDO              sprintDO
     * @param field                 field
     * @return ReportIssueDOList
     */
    List<ReportIssueDO> queryRemoveIssueDoneValueDurationSprint(@Param("issueIdRemoveDoneList") List<Long> issueIdRemoveDoneList, @Param("sprintDO") SprintDO sprintDO, @Param("field") String field);

    /**
     * 获取开启冲刺前，issue状态为done的issueId
     *
     * @param issueIdList issueIdList开启冲刺前冲刺内的issue
     * @param sprintDO    sprintDO
     * @return issueDoneCount
     */
    List<Long> queryDoneIssueIdsBeforeSprintStart(@Param("issueIdList") List<Long> issueIdList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 冲刺期间issue移动到done的时间
     *
     * @param issueIdAddDoneList issueIdAddDoneList冲刺期间移动到done的issue
     * @param sprintDO           sprintDO
     * @return ReportIssueDOList
     */
    List<ReportIssueDO> queryAddIssueDoneDetailDuringSprint(@Param("issueIdAddDoneList") List<Long> issueIdAddDoneList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 冲刺期间issue从done移除的时间
     *
     * @param issueIdRemoveDoneList issueIdAddDoneList冲刺期间移动到非done的issue
     * @param sprintDO              sprintDO
     * @return ReportIssueDOList
     */
    List<ReportIssueDO> queryRemoveIssueDoneDetailDurationSprint(@Param("issueIdRemoveDoneList") List<Long> issueIdRemoveDoneList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 冲刺期间issue的字段值变化（包含变化时间）
     *
     * @param issueAllList issueAllList
     * @param sprintDO     sprintDO
     * @return ReportIssueDOList
     */
    List<ReportIssueDO> queryIssueStoryPointsDurationSprint(@Param("issueAllList") List<Long> issueAllList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 冲刺开启前的issue数量统计信息
     *
     * @param issueIdList issueIdList
     * @param sprintDO    sprintDO
     * @return ReportIssueDO
     */
    List<ReportIssueDO> queryAddIssueBeforeDuringSprint(@Param("issueIdList") List<Long> issueIdList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 查询冲刺结束后的issue数量统计信息
     *
     * @param sprintDO sprintDO
     * @return ReportIssueDO
     */
    List<ReportIssueDO> queryIssueAfterSprint(@Param("sprintDO") SprintDO sprintDO);

    /**
     * 查询冲刺结束后的字段value统计信息
     *
     * @param sprintDO sprintDO
     * @param field    field
     * @return ReportIssueDO
     */
    List<ReportIssueDO> queryIssueValueAfterSprint(@Param("sprintDO") SprintDO sprintDO, @Param("field") String field);

    /**
     * 判断当前时间的issue的修改是否记录统计
     *
     * @param sprintId sprintId
     * @param issueId  issueId
     * @param date     date
     * @return 记录统计true 不计入统计false
     */
    Boolean checkIssueValueIsStatisticalDurationSprint(@Param("sprintId") Long sprintId, @Param("issueId") Long issueId, @Param("date") Date date);

    /**
     * 冲刺期间的剩余预计时间的变更
     *
     * @param issueAllList issueAllList
     * @param sprintDO     sprintDO
     * @return ReportIssueDO
     */
    List<ReportIssueDO> queryIssueTimeesTimateDurationSprint(@Param("issueAllList") List<Long> issueAllList, @Param("sprintDO") SprintDO sprintDO);

    /**
     * 查询issue加入冲刺的时间
     *
     * @param issueId  issueId
     * @param sprintId sprintId
     * @return Date
     */
    Date queryAddIssueDuringSprintNoData(@Param("issueId") Long issueId, @Param("sprintId") Long sprintId);

    List<Long> queryReportIssueIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("actualEndDate") Date actualEndDate, @Param("status") Boolean status);

    List<IssueDO> queryIssueByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<SprintReportIssueStatusDO> queryIssueStoryPoints(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("actualEndDate") Date actualEndDate);

    List<SprintReportIssueStatusDO> queryIssueStatus(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("actualEndDate") Date actualEndDate, @Param("isBefore") boolean isBefore);
}
