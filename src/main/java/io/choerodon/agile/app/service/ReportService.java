package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.GroupDataChartDTO;
import io.choerodon.agile.infra.dataobject.GroupDataChartListDTO;
import io.choerodon.agile.infra.mapper.ReportMapper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

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
     * @param projectId   projectId
     * @param sprintId    sprintId
     * @param type        storyPoints、remainingEstimatedTime、originalEstimatedTime、issueCount
     * @param ordinalType asc、desc
     * @return ReportIssueVO
     */
    List<ReportIssueVO> queryBurnDownReport(Long projectId, Long sprintId, String type, String ordinalType);

    /**
     * 查看累积流量图
     *
     * @param projectId               projectId
     * @param cumulativeFlowFilterVO cumulativeFlowFilterVO
     * @return CumulativeFlowDiagramVO
     */
    List<CumulativeFlowDiagramVO> queryCumulativeFlowDiagram(Long projectId, CumulativeFlowFilterVO cumulativeFlowFilterVO);

    PageInfo<IssueListVO> queryIssueByOptions(Long projectId, Long versionId, String status, String type, PageRequest pageRequest, Long organizationId);

    Map<String, Object> queryVersionLineChart(Long projectId, Long versionId, String type);

    List<VelocitySprintVO> queryVelocityChart(Long projectId, String type);

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
     * @return PieChartVO
     */
    List<PieChartVO> queryPieChart(Long projectId, String fieldName, Long organizationId, Date startDate, Date endDate, Long sprintId, Long versionId);

    List<GroupDataChartDTO> queryEpicChart(Long projectId, Long epicId, String type);

    List<GroupDataChartDTO> queryVersionChart(Long projectId, Long versionId, String type);

    List<GroupDataChartListDTO> queryEpicChartList(Long projectId, Long epicId, Long organizationId);

    List<GroupDataChartListDTO> queryVersionChartList(Long projectId, Long versionId, Long organizationId);

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
     * @return BurnDownReportCoordinateVO
     */
    List<BurnDownReportCoordinateVO> queryBurnDownCoordinateByType(Long projectId, Long id, String type);

    /**
     * 查询epic和版本燃尽图报告信息
     *
     * @param projectId projectId
     * @param id        id
     * @param type      type
     * @return BurnDownReportVO
     */
    BurnDownReportVO queryBurnDownReportByType(Long projectId, Long id, String type, Long organizationId);

    void setReportMapper(ReportMapper reportMapper);

    /**
     * 查询问题类型分布图
     *
     * @param projectId projectId
     * @return IssueTypeDistributionChartVO
     */
    List<IssueTypeDistributionChartVO> queryIssueTypeDistributionChart(Long projectId);

    /**
     * 版本进度图,排序前5个版本
     *
     * @param projectId projectId
     * @return IssueTypeDistributionChartVO
     */
    List<IssueTypeDistributionChartVO> queryVersionProgressChart(Long projectId);

    /**
     * 查询问题优先级分布图
     *
     * @param projectId projectId
     * @return IssuePriorityDistributionChartVO
     */
    List<IssuePriorityDistributionChartVO> queryIssuePriorityDistributionChart(Long projectId, Long organizationId);

    /**
     * 修复累积流图
     */
    void fixCumulativeFlowDiagram();
}
