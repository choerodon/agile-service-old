package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.dataobject.ColumnDTO;
import io.choerodon.agile.infra.dataobject.IssueBurnDownReportDTO;
import io.choerodon.agile.infra.dataobject.SprintDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
@Component
public class ReportAssembler extends AbstractAssembler {

    public List<CumulativeFlowDiagramVO> columnListDTOToVO(List<ColumnDTO> columnDTOList) {
        List<CumulativeFlowDiagramVO> cumulativeFlowDiagramVOList = new ArrayList<>(columnDTOList.size());
        columnDTOList.forEach(columnDTO -> {
            CumulativeFlowDiagramVO cumulativeFlowDiagramVO = new CumulativeFlowDiagramVO();
            cumulativeFlowDiagramVO.setColumnId(columnDTO.getColumnId());
            cumulativeFlowDiagramVO.setColor(columnDTO.getColor());
            cumulativeFlowDiagramVO.setName(columnDTO.getName());
            cumulativeFlowDiagramVO.setCategoryCode(columnDTO.getCategoryCode());
            cumulativeFlowDiagramVOList.add(cumulativeFlowDiagramVO);
        });
        return cumulativeFlowDiagramVOList;
    }

    public SprintBurnDownReportVO sprintBurnDownReportDTOToVO(SprintDTO sprintDTO) {
        SprintBurnDownReportVO sprintBurnDownReportVO = new SprintBurnDownReportVO();
        sprintBurnDownReportVO.setSprintId(sprintDTO.getSprintId());
        sprintBurnDownReportVO.setSprintName(sprintDTO.getSprintName());
        sprintBurnDownReportVO.setStatusCode(sprintDTO.getStatusCode());
        sprintBurnDownReportVO.setStartDate(sprintDTO.getStartDate());
        sprintBurnDownReportVO.setEndDate(sprintDTO.getActualEndDate() == null ? sprintDTO.getEndDate() : sprintDTO.getActualEndDate());
        return sprintBurnDownReportVO;
    }

    public List<IssueBurnDownReportVO> issueBurnDownReportDTOToVO(List<IssueBurnDownReportDTO> issueBurnDownReportDTOS, Map<Long, IssueTypeVO> issueTypeDTOMap,
                                                                  Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, PriorityVO> priorityDTOMap) {
        List<IssueBurnDownReportVO> issueBurnDownReportVOS = new ArrayList<>(issueBurnDownReportDTOS.size());
        if (!issueBurnDownReportDTOS.isEmpty()) {
            issueBurnDownReportDTOS.forEach(issueBurnDownReportDTO -> {
                IssueBurnDownReportVO issueBurnDownReportVO = new IssueBurnDownReportVO();
                BeanUtils.copyProperties(issueBurnDownReportDTO, issueBurnDownReportVO);
                issueBurnDownReportVO.setPriorityVO(priorityDTOMap.get(issueBurnDownReportDTO.getPriorityId()));
                issueBurnDownReportVO.setStatusMapVO(statusMapDTOMap.get(issueBurnDownReportDTO.getStatusId()));
                issueBurnDownReportVO.setIssueTypeVO(issueTypeDTOMap.get(issueBurnDownReportDTO.getIssueTypeId()));
                issueBurnDownReportVOS.add(issueBurnDownReportVO);
            });
        }
        return issueBurnDownReportVOS;
    }

    public List<IssueTypeDistributionChartVO> toIssueTypeDistributionChartVO(Long projectId, List<IssueTypeDistributionChartDTO> issueTypeDistributionChartDTOS) {
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<IssueTypeDistributionChartVO> issueTypeDistributionChartVOS = new ArrayList<>(issueTypeDistributionChartDTOS.size());
        issueTypeDistributionChartDTOS.forEach(issueTypeDistributionChartDTO -> {
            IssueTypeDistributionChartVO issueTypeDistributionChartVO = toTarget(issueTypeDistributionChartDTO, IssueTypeDistributionChartVO.class);
            issueTypeDistributionChartVO.setIssueTypeVO(issueTypeDTOMap.get(issueTypeDistributionChartDTO.getIssueTypeId()));
            issueTypeDistributionChartVO.setName(issueTypeDTOMap.get(issueTypeDistributionChartDTO.getIssueTypeId()).getName());
            issueTypeDistributionChartVO.setStatusMapVO(statusMapDTOMap.get(issueTypeDistributionChartDTO.getStatusId()));
            issueTypeDistributionChartVOS.add(issueTypeDistributionChartVO);
        });
        return issueTypeDistributionChartVOS;
    }

    public List<IssueTypeDistributionChartVO> toIssueTypeVersionDistributionChartVO(Long projectId, List<IssueTypeDistributionChartDTO> issueTypeDistributionChartDTOS) {
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<IssueTypeDistributionChartVO> issueTypeDistributionChartVOS = new ArrayList<>(issueTypeDistributionChartDTOS.size());
        issueTypeDistributionChartDTOS.forEach(issueTypeDistributionChartDTO -> {
            IssueTypeDistributionChartVO issueTypeDistributionChartVO = toTarget(issueTypeDistributionChartDTO, IssueTypeDistributionChartVO.class);
            issueTypeDistributionChartVO.setStatusMapVO(statusMapDTOMap.get(issueTypeDistributionChartDTO.getStatusId()));
            issueTypeDistributionChartVOS.add(issueTypeDistributionChartVO);
        });
        return issueTypeDistributionChartVOS;
    }

    public List<IssuePriorityDistributionChartVO> toIssuePriorityDistributionChartVO(Long projectId, List<IssuePriorityDistributionChartDTO> issuePriorityDistributionChartDTOS) {
        Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        List<IssuePriorityDistributionChartVO> issuePriorityDistributionChartVOS = new ArrayList<>(issuePriorityDistributionChartDTOS.size());
        issuePriorityDistributionChartDTOS.forEach(issuePriorityDistributionChartDTO -> {
            IssuePriorityDistributionChartVO issueTypeDistributionChartDTO = toTarget(issuePriorityDistributionChartDTO, IssuePriorityDistributionChartVO.class);
            issueTypeDistributionChartDTO.setName(priorityDTOMap.get(issuePriorityDistributionChartDTO.getPriorityId()).getName());
            issueTypeDistributionChartDTO.setPriorityVO(priorityDTOMap.get(issuePriorityDistributionChartDTO.getPriorityId()));
            issuePriorityDistributionChartVOS.add(issueTypeDistributionChartDTO);
        });
        return issuePriorityDistributionChartVOS;
    }
}
