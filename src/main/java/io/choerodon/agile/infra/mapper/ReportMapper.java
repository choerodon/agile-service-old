package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.*;
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
     * @param sprintDO sprintDO
     * @return issueIdList
     */
    List<Long> queryAddIssueIdsDuringSprint(@Param("sprintDO") SprintDO sprintDO);

    /**
     * 获取冲刺期间移除的issue(不包含子任务和epic)
     *
     * @param sprintDO sprintDO
     * @return issueIdList
     */
    List<Long> queryRemoveIssueIdsDuringSprintWithOutSubEpicIssue(@Param("sprintDO") SprintDO sprintDO);

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
     * @param field        field
     * @return ReportIssueDOList
     */
    List<ReportIssueDO> queryIssueChangeValueDurationSprint(@Param("issueAllList") List<Long> issueAllList, @Param("sprintDO") SprintDO sprintDO, @Param("field") String field);

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
    List<ReportIssueDO> queryIssueCountAfterSprint(@Param("sprintDO") SprintDO sprintDO);

    /**
     * 查询冲刺结束后的字段value统计信息
     *
     * @param sprintDO sprintDO
     * @param field    field
     * @return ReportIssueDO
     */
    List<ReportIssueDO> queryIssueValueAfterSprint(@Param("sprintDO") SprintDO sprintDO, @Param("field") String field);

    /**
     * 查询issue加入冲刺的时间
     *
     * @param issueId  issueId
     * @param sprintId sprintId
     * @return Date
     */
    Date queryAddIssueDuringSprintNoData(@Param("issueId") Long issueId, @Param("sprintId") Long sprintId);

    List<Long> queryReportIssueIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("startDate") Date startDate, @Param("actualEndDate") Date actualEndDate, @Param("status") Boolean status);

    List<IssueDO> queryIssueByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<SprintReportIssueStatusDO> queryIssueStoryPoints(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("actualEndDate") Date actualEndDate);

    /**
     * 获取快捷创建的issue的字段值
     *
     * @param issueId issueId
     * @param date    date
     * @param field   field
     * @return Integer
     */
    Integer queryAddIssueValueDuringSprintNoData(@Param("issueId") Long issueId, @Param("date") Date date, @Param("field") String field);

    List<SprintReportIssueStatusDO> queryBeforeIssueStatus(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("startDate") Date startDate, @Param("actualEndDate") Date actualEndDate);

    List<SprintReportIssueStatusDO> queryAfterIssueStatus(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("actualEndDate") Date actualEndDate);

    /**
     * issue在当前日期状态是否为done
     *
     * @param issueId issueId
     * @param date    date
     * @return 为done返回true，否返回false
     */
    Boolean checkIssueDoneStatus(@Param("issueId") Long issueId, @Param("date") Date date);

    /**
     * 冲刺期间移出的issue
     *
     * @param sprintDO sprintDO
     * @return Long
     */
    List<Long> queryRemoveIssueIdsDuringSprint(@Param("sprintDO") SprintDO sprintDO);

    /**
     * 判断issue是否在冲刺外
     *
     * @param issueId  issueId
     * @param date     date
     * @param sprintId sprintId
     * @return Boolean
     */
    Boolean checkIssueRemove(@Param("issueId") Long issueId, @Param("date") Date date, @Param("sprintId") Long sprintId);

    /**
     * 获取累积流量图符合条件的所有issue
     *
     * @param projectId projectId
     * @param filterSql filterSql
     * @return Long
     */
    List<Long> queryAllIssueIdsByFilter(@Param("projectId") Long projectId, @Param("filterSql") String filterSql);

    /**
     * 获取累积流量图符合条件的所有issue
     *
     * @param startDate   startDate
     * @param endDate     endDate
     * @param allIssueIds allIssueIds
     * @param columnIds   columnIds
     * @return ColumnChangeDO
     */
    List<ColumnChangeDO> queryAddIssueDuringDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("allIssueIds") List<Long> allIssueIds, @Param("columnIds") List<Long> columnIds);

    /**
     * 查询时间范围内的列变化（累积流图）
     *
     * @param startDate   startDate
     * @param endDate     endDate
     * @param allIssueIds allIssueIds
     * @param columnIds   columnIds
     * @return ColumnChangeDO
     */
    List<ColumnChangeDO> queryChangeIssueDuringDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("allIssueIds") List<Long> allIssueIds, @Param("columnIds") List<Long> columnIds);

    List queryReportIssues(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("status") String status);
}
