package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.infra.dataobject.GroupDataChartDO;
import io.choerodon.agile.infra.dataobject.GroupDataChartListDO;
import io.choerodon.agile.infra.mapper.ReportMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public interface ReportService {

    /**
     * 燃尽图报告信息
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @param type      storyPoints、remainingEstimatedTime、originalEstimatedTime、issueCount
     * @return ReportIssueDTO
     */
    List<ReportIssueDTO> queryBurnDownReport(Long projectId, Long sprintId, String type);

    /**
     * 查看累积流量图
     *
     * @param projectId               projectId
     * @param cumulativeFlowFilterDTO cumulativeFlowFilterDTO
     * @return CumulativeFlowDiagramDTO
     */
    List<CumulativeFlowDiagramDTO> queryCumulativeFlowDiagram(Long projectId, CumulativeFlowFilterDTO cumulativeFlowFilterDTO);

    Page<IssueListDTO> queryIssueByOptions(Long projectId, Long versionId, String status, String type, PageRequest pageRequest, Long organizationId);

    Map<String, Object> queryVersionLineChart(Long projectId, Long versionId, String type);

    List<VelocitySprintDTO> queryVelocityChart(Long projectId, String type);

    /**
     * 根据项目id和字段名称查询饼图
     *
     * @param projectId      projectId
     * @param fieldName      fieldName
     * @param organizationId organizationId
     * @param startDate      startDate
     * @param endDate        endDate
     * @param sprintId       sprintId
     * @param versionId      versionId
     * @return PieChartDTO
     */
    List<PieChartDTO> queryPieChart(Long projectId, String fieldName, Long organizationId, Date startDate, Date endDate, Long sprintId, Long versionId);

    List<GroupDataChartDO> queryEpicChart(Long projectId, Long epicId, String type);

    List<GroupDataChartDO> queryVersionChart(Long projectId, Long versionId, String type);

    List<GroupDataChartListDO> queryEpicChartList(Long projectId, Long epicId, Long organizationId);

    List<GroupDataChartListDO> queryVersionChartList(Long projectId, Long versionId, Long organizationId);

    /**
     * 查询燃尽图坐标信息
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @param type      type
     * @return Coordinate
     */
    JSONObject queryBurnDownCoordinate(Long projectId, Long sprintId, String type);

    /**
     * 查询epic和版本燃耗图坐标信息
     *
     * @param projectId projectId
     * @param id        id
     * @param type      type
     * @return BurnDownReportCoordinateDTO
     */
    List<BurnDownReportCoordinateDTO> queryBurnDownCoordinateByType(Long projectId, Long id, String type);

    /**
     * 查询epic和版本燃尽图报告信息
     *
     * @param projectId projectId
     * @param id        id
     * @param type      type
     * @return BurnDownReportDTO
     */
    BurnDownReportDTO queryBurnDownReportByType(Long projectId, Long id, String type, Long organizationId);

    void setReportMapper(ReportMapper reportMapper);

    /**
     * 查询问题类型分布图
     *
     * @param projectId projectId
     * @return IssueTypeDistributionChartDTO
     */
    List<IssueTypeDistributionChartDTO> queryIssueTypeDistributionChart(Long projectId);

    /**
     * 版本进度图,排序前5个版本
     *
     * @param projectId projectId
     * @return IssueTypeDistributionChartDTO
     */
    List<IssueTypeDistributionChartDTO> queryVersionProgressChart(Long projectId);

    /**
     * 查询问题优先级分布图
     *
     * @param projectId projectId
     * @return IssuePriorityDistributionChartDTO
     */
    List<IssuePriorityDistributionChartDTO> queryIssuePriorityDistributionChart(Long projectId, Long organizationId);
}
