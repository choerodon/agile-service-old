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

    public List<CumulativeFlowDiagramDTO> columnListDoToDto(List<ColumnDO> columnDOList) {
        List<CumulativeFlowDiagramDTO> cumulativeFlowDiagramDTOList = new ArrayList<>(columnDOList.size());
        columnDOList.forEach(columnDO -> {
            CumulativeFlowDiagramDTO cumulativeFlowDiagramDTO = new CumulativeFlowDiagramDTO();
            cumulativeFlowDiagramDTO.setColumnId(columnDO.getColumnId());
            cumulativeFlowDiagramDTO.setColor(columnDO.getColor());
            cumulativeFlowDiagramDTO.setName(columnDO.getName());
            cumulativeFlowDiagramDTO.setCategoryCode(columnDO.getCategoryCode());
            cumulativeFlowDiagramDTOList.add(cumulativeFlowDiagramDTO);
        });
        return cumulativeFlowDiagramDTOList;
    }

    public SprintBurnDownReportDTO sprintBurnDownReportDoToDto(SprintDTO sprintDTO) {
        SprintBurnDownReportDTO sprintBurnDownReportDTO = new SprintBurnDownReportDTO();
        sprintBurnDownReportDTO.setSprintId(sprintDTO.getSprintId());
        sprintBurnDownReportDTO.setSprintName(sprintDTO.getSprintName());
        sprintBurnDownReportDTO.setStatusCode(sprintDTO.getStatusCode());
        sprintBurnDownReportDTO.setStartDate(sprintDTO.getStartDate());
        sprintBurnDownReportDTO.setEndDate(sprintDTO.getActualEndDate() == null ? sprintDTO.getEndDate() : sprintDTO.getActualEndDate());
        return sprintBurnDownReportDTO;
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

    public List<IssueTypeDistributionChartDTO> toIssueTypeDistributionChartDTO(Long projectId, List<IssueTypeDistributionChartDO> issueTypeDistributionChartDOS) {
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<IssueTypeDistributionChartDTO> issueTypeDistributionChartDTOS = new ArrayList<>(issueTypeDistributionChartDOS.size());
        issueTypeDistributionChartDOS.forEach(issueTypeDistributionChartDO -> {
            IssueTypeDistributionChartDTO issueTypeDistributionChartDTO = toTarget(issueTypeDistributionChartDO, IssueTypeDistributionChartDTO.class);
            issueTypeDistributionChartDTO.setIssueTypeVO(issueTypeDTOMap.get(issueTypeDistributionChartDO.getIssueTypeId()));
            issueTypeDistributionChartDTO.setName(issueTypeDTOMap.get(issueTypeDistributionChartDO.getIssueTypeId()).getName());
            issueTypeDistributionChartDTO.setStatusMapVO(statusMapDTOMap.get(issueTypeDistributionChartDO.getStatusId()));
            issueTypeDistributionChartDTOS.add(issueTypeDistributionChartDTO);
        });
        return issueTypeDistributionChartDTOS;
    }

    public List<IssueTypeDistributionChartDTO> toIssueTypeVersionDistributionChartDTO(Long projectId, List<IssueTypeDistributionChartDO> issueTypeDistributionChartDOS) {
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<IssueTypeDistributionChartDTO> issueTypeDistributionChartDTOS = new ArrayList<>(issueTypeDistributionChartDOS.size());
        issueTypeDistributionChartDOS.forEach(issueTypeDistributionChartDO -> {
            IssueTypeDistributionChartDTO issueTypeDistributionChartDTO = toTarget(issueTypeDistributionChartDO, IssueTypeDistributionChartDTO.class);
            issueTypeDistributionChartDTO.setStatusMapVO(statusMapDTOMap.get(issueTypeDistributionChartDO.getStatusId()));
            issueTypeDistributionChartDTOS.add(issueTypeDistributionChartDTO);
        });
        return issueTypeDistributionChartDTOS;
    }

    public List<IssuePriorityDistributionChartDTO> toIssuePriorityDistributionChartDTO(Long projectId, List<IssuePriorityDistributionChartDO> issuePriorityDistributionChartDOS) {
        Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        List<IssuePriorityDistributionChartDTO> issuePriorityDistributionChartDTOS = new ArrayList<>(issuePriorityDistributionChartDOS.size());
        issuePriorityDistributionChartDOS.forEach(issuePriorityDistributionChartDO -> {
            IssuePriorityDistributionChartDTO issueTypeDistributionChartDTO = toTarget(issuePriorityDistributionChartDO, IssuePriorityDistributionChartDTO.class);
            issueTypeDistributionChartDTO.setName(priorityDTOMap.get(issuePriorityDistributionChartDO.getPriorityId()).getName());
            issueTypeDistributionChartDTO.setPriorityVO(priorityDTOMap.get(issuePriorityDistributionChartDO.getPriorityId()));
            issuePriorityDistributionChartDTOS.add(issueTypeDistributionChartDTO);
        });
        return issuePriorityDistributionChartDTOS;
    }
}
