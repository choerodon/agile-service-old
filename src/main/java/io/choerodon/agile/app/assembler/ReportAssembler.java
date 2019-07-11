package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.dataobject.ColumnDO;
import io.choerodon.agile.infra.dataobject.IssueBurnDownReportDO;
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

    public List<CumulativeFlowDiagramVO> columnListDoToDto(List<ColumnDO> columnDOList) {
        List<CumulativeFlowDiagramVO> cumulativeFlowDiagramVOList = new ArrayList<>(columnDOList.size());
        columnDOList.forEach(columnDO -> {
            CumulativeFlowDiagramVO cumulativeFlowDiagramVO = new CumulativeFlowDiagramVO();
            cumulativeFlowDiagramVO.setColumnId(columnDO.getColumnId());
            cumulativeFlowDiagramVO.setColor(columnDO.getColor());
            cumulativeFlowDiagramVO.setName(columnDO.getName());
            cumulativeFlowDiagramVO.setCategoryCode(columnDO.getCategoryCode());
            cumulativeFlowDiagramVOList.add(cumulativeFlowDiagramVO);
        });
        return cumulativeFlowDiagramVOList;
    }

    public SprintBurnDownReportVO sprintBurnDownReportDoToDto(SprintDTO sprintDTO) {
        SprintBurnDownReportVO sprintBurnDownReportVO = new SprintBurnDownReportVO();
        sprintBurnDownReportVO.setSprintId(sprintDTO.getSprintId());
        sprintBurnDownReportVO.setSprintName(sprintDTO.getSprintName());
        sprintBurnDownReportVO.setStatusCode(sprintDTO.getStatusCode());
        sprintBurnDownReportVO.setStartDate(sprintDTO.getStartDate());
        sprintBurnDownReportVO.setEndDate(sprintDTO.getActualEndDate() == null ? sprintDTO.getEndDate() : sprintDTO.getActualEndDate());
        return sprintBurnDownReportVO;
    }

    public List<IssueBurnDownReportDTO> issueBurnDownReportDoToDto(List<IssueBurnDownReportDO> issueBurnDownReportDOS, Map<Long, IssueTypeVO> issueTypeDTOMap,
                                                                   Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, PriorityVO> priorityDTOMap) {
        List<IssueBurnDownReportDTO> issueBurnDownReportDTOS = new ArrayList<>(issueBurnDownReportDOS.size());
        if (!issueBurnDownReportDOS.isEmpty()) {
            issueBurnDownReportDOS.forEach(issueBurnDownReportDO -> {
                IssueBurnDownReportDTO issueBurnDownReportDTO = new IssueBurnDownReportDTO();
                BeanUtils.copyProperties(issueBurnDownReportDO, issueBurnDownReportDTO);
                issueBurnDownReportDTO.setPriorityVO(priorityDTOMap.get(issueBurnDownReportDO.getPriorityId()));
                issueBurnDownReportDTO.setStatusMapVO(statusMapDTOMap.get(issueBurnDownReportDO.getStatusId()));
                issueBurnDownReportDTO.setIssueTypeVO(issueTypeDTOMap.get(issueBurnDownReportDO.getIssueTypeId()));
                issueBurnDownReportDTOS.add(issueBurnDownReportDTO);
            });
        }
        return issueBurnDownReportDTOS;
    }

    public List<IssueTypeDistributionChartVO> toIssueTypeDistributionChartDTO(Long projectId, List<IssueTypeDistributionChartDO> issueTypeDistributionChartDOS) {
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<IssueTypeDistributionChartVO> issueTypeDistributionChartVOS = new ArrayList<>(issueTypeDistributionChartDOS.size());
        issueTypeDistributionChartDOS.forEach(issueTypeDistributionChartDO -> {
            IssueTypeDistributionChartVO issueTypeDistributionChartVO = toTarget(issueTypeDistributionChartDO, IssueTypeDistributionChartVO.class);
            issueTypeDistributionChartVO.setIssueTypeVO(issueTypeDTOMap.get(issueTypeDistributionChartDO.getIssueTypeId()));
            issueTypeDistributionChartVO.setName(issueTypeDTOMap.get(issueTypeDistributionChartDO.getIssueTypeId()).getName());
            issueTypeDistributionChartVO.setStatusMapVO(statusMapDTOMap.get(issueTypeDistributionChartDO.getStatusId()));
            issueTypeDistributionChartVOS.add(issueTypeDistributionChartVO);
        });
        return issueTypeDistributionChartVOS;
    }

    public List<IssueTypeDistributionChartVO> toIssueTypeVersionDistributionChartDTO(Long projectId, List<IssueTypeDistributionChartDO> issueTypeDistributionChartDOS) {
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<IssueTypeDistributionChartVO> issueTypeDistributionChartVOS = new ArrayList<>(issueTypeDistributionChartDOS.size());
        issueTypeDistributionChartDOS.forEach(issueTypeDistributionChartDO -> {
            IssueTypeDistributionChartVO issueTypeDistributionChartVO = toTarget(issueTypeDistributionChartDO, IssueTypeDistributionChartVO.class);
            issueTypeDistributionChartVO.setStatusMapVO(statusMapDTOMap.get(issueTypeDistributionChartDO.getStatusId()));
            issueTypeDistributionChartVOS.add(issueTypeDistributionChartVO);
        });
        return issueTypeDistributionChartVOS;
    }

    public List<IssuePriorityDistributionChartVO> toIssuePriorityDistributionChartDTO(Long projectId, List<IssuePriorityDistributionChartDO> issuePriorityDistributionChartDOS) {
        Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        List<IssuePriorityDistributionChartVO> issuePriorityDistributionChartVOS = new ArrayList<>(issuePriorityDistributionChartDOS.size());
        issuePriorityDistributionChartDOS.forEach(issuePriorityDistributionChartDO -> {
            IssuePriorityDistributionChartVO issueTypeDistributionChartDTO = toTarget(issuePriorityDistributionChartDO, IssuePriorityDistributionChartVO.class);
            issueTypeDistributionChartDTO.setName(priorityDTOMap.get(issuePriorityDistributionChartDO.getPriorityId()).getName());
            issueTypeDistributionChartDTO.setPriorityVO(priorityDTOMap.get(issuePriorityDistributionChartDO.getPriorityId()));
            issuePriorityDistributionChartVOS.add(issueTypeDistributionChartDTO);
        });
        return issuePriorityDistributionChartVOS;
    }
}
