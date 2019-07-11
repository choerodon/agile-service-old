package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public interface ReportMapper {

    /**
     * 获取当前冲刺开启前的issue的指定修改字段信息
     *
     * @param issueIdList issueIdList当前冲刺开启前的issueIdList
     * @param sprintDTO    sprintDTO
     * @param field       filed修改字段
     * @return ReportIssueDTO
     */
    List<ReportIssueDTO> queryValueBeforeSprintStart(@Param("issueIdList") List<Long> issueIdList, @Param("sprintDTO") SprintDTO sprintDTO, @Param("field") String field);

    /**
     * 获取当前冲刺期间加入的issue(包含加入时间、加入时的字段值)
     *
     * @param issueIdAddList issueIdList当前冲刺期间加入的issueIdList
     * @param sprintDTO       sprintDTO
     * @param field          filed修改字段
     * @return ReportIssueConvertDTO
     */
    List<ReportIssueDTO> queryAddIssueValueDuringSprint(@Param("issueIdAddList") List<Long> issueIdAddList, @Param("sprintDTO") SprintDTO sprintDTO, @Param("field") String field);

    /**
     * 获取当前冲刺期间移除的issue(包含移除时间、移除时的字段值)
     *
     * @param issueIdRemoveList issueIdRemoveList
     * @param sprintDTO          sprintDTO
     * @param field             filed修改字段
     * @return ReportIssueConvertDTO
     */
    List<ReportIssueDTO> queryRemoveIssueValueDurationSprint(@Param("issueIdRemoveList") List<Long> issueIdRemoveList, @Param("sprintDTO") SprintDTO sprintDTO, @Param("field") String field);

    /**
     * 获取冲刺开启前的issue
     *
     * @param sprintDTO sprintDTO
     * @return issueIds
     */
    List<Long> queryIssueIdsBeforeSprintStart(@Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 获取冲刺期间加入的issue
     *
     * @param sprintDTO sprintDTO
     * @return issueIdList
     */
    List<Long> queryAddIssueIdsDuringSprint(@Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 获取冲刺期间移除的issue(不包含子任务和epic)
     *
     * @param sprintDTO sprintDTO
     * @return issueIdList
     */
    List<Long> queryRemoveIssueIdsDuringSprintWithOutSubEpicIssue(@Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 查询在冲刺期间添加的issue，包含issue加入的时间
     *
     * @param issueIdAddList issueIdAddList
     * @param sprintDTO       sprintDTO
     * @return ReportIssueDOList
     */
    List<ReportIssueDTO> queryAddIssueDuringSprint(@Param("issueIdAddList") List<Long> issueIdAddList, @Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 查询在冲刺期间移除的issue，包含issue移除的时间
     *
     * @param issueIdRemoveList issueIdRemoveList
     * @param sprintDTO          sprintDTO
     * @return ReportIssueDOList
     */
    List<ReportIssueDTO> queryRemoveIssueDuringSprint(@Param("issueIdRemoveList") List<Long> issueIdRemoveList, @Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 获取冲刺期间issue状态更改为done的issue
     *
     * @param sprintDTO     sprintDTO
     * @param issueAllList issueAllList
     * @return issueIds
     */
    List<Long> queryAddDoneIssueIdsDuringSprint(@Param("sprintDTO") SprintDTO sprintDTO, @Param("issueAllList") List<Long> issueAllList);

    /**
     * 获取冲刺期间issue状态从done更改到其他的issue
     *
     * @param sprintDTO     sprintDTO
     * @param issueAllList issueAllList
     * @return issueIds
     */
    List<Long> queryRemoveDoneIssueIdsDuringSprint(@Param("sprintDTO") SprintDTO sprintDTO, @Param("issueAllList") List<Long> issueAllList);

    /**
     * 获取冲刺期间移动到done状态的字段变更值（包含变更时间）
     *
     * @param issueId  issueId
     * @param sprintDTO sprintDTO
     * @param field    field
     * @return ReportIssueDOList
     */
    List<ReportIssueDTO> queryAddIssueDoneValueDuringSprint(@Param("issueId") Long issueId, @Param("sprintDTO") SprintDTO sprintDTO, @Param("field") String field);

    /**
     * 获取冲刺期间done移动到非done状态的字段变更值（包含变更时间）
     *
     * @param issueId  issueId
     * @param sprintDTO sprintDTO
     * @param field    field
     * @return ReportIssueDOList
     */
    List<ReportIssueDTO> queryRemoveIssueDoneValueDurationSprint(@Param("issueId") Long issueId, @Param("sprintDTO") SprintDTO sprintDTO, @Param("field") String field);

    /**
     * 获取开启冲刺前，issue状态为done的issueId
     *
     * @param issueIdList issueIdList开启冲刺前冲刺内的issue
     * @param sprintDTO    sprintDTO
     * @return issueDoneCount
     */
    List<Long> queryDoneIssueIdsBeforeSprintStart(@Param("issueIdList") List<Long> issueIdList, @Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 冲刺期间issue移动到done的时间
     *
     * @param issueIdAddDoneList issueIdAddDoneList冲刺期间移动到done的issue
     * @param sprintDTO           sprintDTO
     * @return ReportIssueDOList
     */
    List<ReportIssueDTO> queryAddIssueDoneDetailDuringSprint(@Param("issueIdAddDoneList") List<Long> issueIdAddDoneList, @Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 冲刺期间issue从done移除的时间
     *
     * @param issueIdRemoveDoneList issueIdAddDoneList冲刺期间移动到非done的issue
     * @param sprintDTO              sprintDTO
     * @return ReportIssueDOList
     */
    List<ReportIssueDTO> queryRemoveIssueDoneDetailDurationSprint(@Param("issueIdRemoveDoneList") List<Long> issueIdRemoveDoneList, @Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 冲刺期间issue的字段值变化（包含变化时间）
     *
     * @param issueAllList issueAllList
     * @param sprintDTO     sprintDTO
     * @param field        field
     * @return ReportIssueDOList
     */
    List<ReportIssueDTO> queryIssueChangeValueDurationSprint(@Param("issueAllList") List<Long> issueAllList, @Param("sprintDTO") SprintDTO sprintDTO, @Param("field") String field);

    /**
     * 冲刺开启前的issue数量统计信息
     *
     * @param issueIdList issueIdList
     * @param sprintDTO    sprintDTO
     * @return ReportIssueDTO
     */
    List<ReportIssueDTO> queryAddIssueBeforeDuringSprint(@Param("issueIdList") List<Long> issueIdList, @Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 查询冲刺结束后的issue数量统计信息
     *
     * @param sprintDTO sprintDTO
     * @return ReportIssueDTO
     */
    List<ReportIssueDTO> queryIssueCountAfterSprint(@Param("sprintDTO") SprintDTO sprintDTO);

    /**
     * 查询冲刺结束后的字段value统计信息
     *
     * @param sprintDTO sprintDTO
     * @param field    field
     * @return ReportIssueDTO
     */
    List<ReportIssueDTO> queryIssueValueAfterSprint(@Param("sprintDTO") SprintDTO sprintDTO, @Param("field") String field);

    List<Long> queryReportIssueIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("startDate") Date startDate, @Param("actualEndDate") Date actualEndDate, @Param("status") Boolean status);

    List<IssueDTO> queryIssueByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<SprintReportIssueStatusDO> queryIssueStoryPoints(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("actualEndDate") Date actualEndDate);

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
     * @param sprintDTO sprintDTO
     * @return Long
     */
    List<Long> queryRemoveIssueIdsDuringSprint(@Param("sprintDTO") SprintDTO sprintDTO);

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
     * @return ColumnChangeDTO
     */
    List<ColumnChangeDTO> queryAddIssueDuringDate(@Param("projectId") Long projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("allIssueIds") List<Long> allIssueIds, @Param("columnIds") List<Long> columnIds);

    /**
     * 查询时间范围内的列变化（累积流图）
     *
     * @param startDate   startDate
     * @param endDate     endDate
     * @param allIssueIds allIssueIds
     * @param columnIds   columnIds
     * @return ColumnChangeDTO
     */
    List<ColumnChangeDTO> queryChangeIssueDuringDate(@Param("projectId") Long projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("allIssueIds") List<Long> allIssueIds, @Param("columnIds") List<Long> columnIds);

    List queryReportIssues(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("status") String status, @Param("type") String type);

    List<VersionIssueChangeDO> queryChangeIssue(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<Long> queryIssueIdByVersionId(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    List<IssueChangeDTO> queryChangeFieldIssue(@Param("projectId") Long projectId, @Param("versionIssues") List<VersionIssueChangeDO> versionIssues, @Param("field") String field);

    List<VersionIssueChangeDO> queryCompletedChangeIssue(@Param("projectId") Long projectId, @Param("versionIssues") List<VersionIssueChangeDO> versionIssues, @Param("completed") Boolean completed);

    List<IssueChangeDTO> queryChangIssue(@Param("projectId") Long projectId, @Param("changeIssues") List<VersionIssueChangeDO> changeIssues, @Param("field") String field);

    Integer queryTotalField(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("field") String field);

    Integer queryCompleteField(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("field") String field);

    Integer queryUnEstimateCount(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("field") String field);

    Integer queryCompletedIssueCount(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<VelocitySprintDO> selectAllSprint(@Param("projectId") Long projectId);

    List<VelocitySingleDO> selectByIssueCountCommitted(@Param("projectId") Long projectId, @Param("ids") List<Long> ids, @Param("now") String now);

    List<VelocitySingleDO> selectByIssueCountCompleted(@Param("projectId") Long projectId, @Param("ids") List<Long> ids, @Param("now") String now);

    List<VelocitySingleDO> selectByStoryPointAndNumCommitted(@Param("projectId") Long projectId, @Param("ids") List<Long> ids, @Param("now") String now);

    List<VelocitySingleDO> selectByStoryPointAndNumCompleted(@Param("projectId") Long projectId, @Param("ids") List<Long> ids, @Param("now") String now);

    List<VelocitySingleDO> selectByRemainTimeCommitted(@Param("projectId") Long projectId, @Param("ids") List<Long> ids, @Param("now") String now);

    List<VelocitySingleDO> selectByRemainTimeCompleted(@Param("projectId") Long projectId, @Param("ids") List<Long> ids, @Param("now") String now);

    /**
     * 根据参数查询统计信息
     *
     * @param projectId projectId
     * @param own       是否是自身表字段 是true 不是false
     * @param fieldName fieldName 字段名
     * @param typeCode  是否关联键值表 是true 不是false
     * @param total     total
     * @param startDate startDate
     * @param endDate   endDate
     * @param sprintId  sprintId
     * @param versionId versionId
     * @return PieChartDO
     */
    List<PieChartDO> queryPieChartByParam(@Param("projectId") Long projectId, @Param("own") Boolean own,
                                          @Param("fieldName") String fieldName, @Param("typeCode") Boolean typeCode,
                                          @Param("total") Integer total, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("sprintId") Long sprintId, @Param("versionId") Long versionId);

    /**
     * 根据Epic查询统计信息
     *
     * @param projectId projectId
     * @param total     total
     * @param startDate startDate
     * @param endDate   endDate
     * @param sprintId  sprintId
     * @param versionId versionId
     * @return PieChartDO
     */
    List<PieChartDO> queryPieChartByEpic(@Param("projectId") Long projectId, @Param("total") Integer total, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("sprintId") Long sprintId, @Param("versionId") Long versionId);


    /**
     * 根据参数查询issue总数
     *
     * @param projectId projectId
     * @param fieldName fieldName
     * @param startDate startDate
     * @param endDate   endDate
     * @param sprintId  sprintId
     * @param versionId versionId
     * @return Integer
     */
    Integer queryIssueCountByFieldName(@Param("projectId") Long projectId, @Param("fieldName") String fieldName,
                                       @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("sprintId") Long sprintId, @Param("versionId") Long versionId);

    List<GroupDataChartListDTO> selectEpicIssueList(@Param("projectId") Long projectId, @Param("epicId") Long epicId);

    List<GroupDataChartListDTO> selectVersionIssueList(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    List<GroupDataChartDTO> selectByStoryPointCompletedFinal(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByStoryPointAllFinal(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByStoryPointCountAll(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByStoryPointCountEstimate(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByRemainTimeRemainCompleted(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByRemainTimeWorkLogCompleted(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByRemainTimeRemainAll(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByRemainTimeWorkLogAll(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByRemainTimeCountAll(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByRemainTimeCountEstimate(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByIssueCountCompletedFinal(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    List<GroupDataChartDTO> selectByIssueCountAllFinal(@Param("projectId") Long projectId, @Param("id") Long id, @Param("chartType") String chartType);

    /**
     * 问题类型分布图
     *
     * @param projectId projectId
     * @return IssueTypeDistributionChartDO
     */
    List<IssueTypeDistributionChartDO> queryIssueTypeDistributionChart(@Param("projectId") Long projectId);

    /**
     * 问题类型分布图,排序前5个版本
     *
     * @param projectId projectId
     * @return IssueTypeDistributionChartDO
     */
    List<IssueTypeDistributionChartDO> queryVersionProgressChart(@Param("projectId") Long projectId);

    /**
     * 问题优先级分布图
     *
     * @param projectId projectId
     * @return IssuePriorityDistributionChartDO
     */
    List<IssuePriorityDistributionChartDO> queryIssuePriorityDistributionChart(@Param("projectId") Long projectId, @Param("priorityIds") List<Long> priorityIds);

    /**
     * 修复数据
     *
     * @param issueId issueId
     * @return FixCumulativeData
     */
    List<FixCumulativeData> queryFixCumulativeData(@Param("issueId") Long issueId);

    /**
     * 获取需要修复的issue
     *
     * @return IssueDTO
     */
    Set<Long> queryIssueDOByFixCumulativeData();

    /**
     * 排除有问题数据的issue
     *
     * @return IssueDTO
     */
    Set<Long> queryRemoveIssueIds();

    ReportIssueDTO queryLastResolutionBeforeMoveOutSprint(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("outDate") Date outDate);
}
