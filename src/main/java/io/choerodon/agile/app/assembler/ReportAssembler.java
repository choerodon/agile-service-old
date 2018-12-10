package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.dataobject.ColumnDO;
import io.choerodon.agile.infra.dataobject.IssueBurnDownReportDO;
import io.choerodon.agile.infra.dataobject.SprintDO;
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

    public SprintBurnDownReportDTO sprintBurnDownReportDoToDto(SprintDO sprintDO) {
        SprintBurnDownReportDTO sprintBurnDownReportDTO = new SprintBurnDownReportDTO();
        sprintBurnDownReportDTO.setSprintId(sprintDO.getSprintId());
        sprintBurnDownReportDTO.setSprintName(sprintDO.getSprintName());
        sprintBurnDownReportDTO.setStatusCode(sprintDO.getStatusCode());
        sprintBurnDownReportDTO.setStartDate(sprintDO.getStartDate());
        sprintBurnDownReportDTO.setEndDate(sprintDO.getActualEndDate() == null ? sprintDO.getEndDate() : sprintDO.getActualEndDate());
        return sprintBurnDownReportDTO;
    }

    public List<IssueBurnDownReportDTO> issueBurnDownReportDoToDto(List<IssueBurnDownReportDO> issueBurnDownReportDOS, Map<Long, IssueTypeDTO> issueTypeDTOMap,
                                                                   Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, PriorityDTO> priorityDTOMap) {
        List<IssueBurnDownReportDTO> issueBurnDownReportDTOS = new ArrayList<>(issueBurnDownReportDOS.size());
        if (!issueBurnDownReportDOS.isEmpty()) {
            issueBurnDownReportDOS.forEach(issueBurnDownReportDO -> {
                IssueBurnDownReportDTO issueBurnDownReportDTO = new IssueBurnDownReportDTO();
                BeanUtils.copyProperties(issueBurnDownReportDO, issueBurnDownReportDTO);
                issueBurnDownReportDTO.setPriorityDTO(priorityDTOMap.get(issueBurnDownReportDO.getPriorityId()));
                issueBurnDownReportDTO.setStatusMapDTO(statusMapDTOMap.get(issueBurnDownReportDO.getStatusId()));
                issueBurnDownReportDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueBurnDownReportDO.getIssueTypeId()));
//                issueBurnDownReportDTO.setStatusColor(ColorUtil.initializationStatusColor(issueBurnDownReportDTO.getStatusCode(), lookupValueMap));
                issueBurnDownReportDTOS.add(issueBurnDownReportDTO);
            });
        }
        return issueBurnDownReportDTOS;
    }

    public List<IssueTypeDistributionChartDTO> toIssueTypeDistributionChartDTO(Long projectId, List<IssueTypeDistributionChartDO> issueTypeDistributionChartDOS) {
        Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<IssueTypeDistributionChartDTO> issueTypeDistributionChartDTOS = new ArrayList<>(issueTypeDistributionChartDOS.size());
        issueTypeDistributionChartDOS.forEach(issueTypeDistributionChartDO -> {
            IssueTypeDistributionChartDTO issueTypeDistributionChartDTO = toTarget(issueTypeDistributionChartDO, IssueTypeDistributionChartDTO.class);
            issueTypeDistributionChartDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueTypeDistributionChartDO.getIssueTypeId()));
            issueTypeDistributionChartDTO.setName(issueTypeDTOMap.get(issueTypeDistributionChartDO.getIssueTypeId()).getName());
            issueTypeDistributionChartDTO.setStatusMapDTO(statusMapDTOMap.get(issueTypeDistributionChartDO.getStatusId()));
            issueTypeDistributionChartDTOS.add(issueTypeDistributionChartDTO);
        });
        return issueTypeDistributionChartDTOS;
    }

    public List<IssueTypeDistributionChartDTO> toIssueTypeVersionDistributionChartDTO(Long projectId, List<IssueTypeDistributionChartDO> issueTypeDistributionChartDOS) {
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        List<IssueTypeDistributionChartDTO> issueTypeDistributionChartDTOS = new ArrayList<>(issueTypeDistributionChartDOS.size());
        issueTypeDistributionChartDOS.forEach(issueTypeDistributionChartDO -> {
            IssueTypeDistributionChartDTO issueTypeDistributionChartDTO = toTarget(issueTypeDistributionChartDO, IssueTypeDistributionChartDTO.class);
            issueTypeDistributionChartDTO.setStatusMapDTO(statusMapDTOMap.get(issueTypeDistributionChartDO.getStatusId()));
            issueTypeDistributionChartDTOS.add(issueTypeDistributionChartDTO);
        });
        return issueTypeDistributionChartDTOS;
    }

    public List<IssuePriorityDistributionChartDTO> toIssuePriorityDistributionChartDTO(Long projectId, List<IssuePriorityDistributionChartDO> issuePriorityDistributionChartDOS) {
        Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        List<IssuePriorityDistributionChartDTO> issuePriorityDistributionChartDTOS = new ArrayList<>(issuePriorityDistributionChartDOS.size());
        issuePriorityDistributionChartDOS.forEach(issuePriorityDistributionChartDO -> {
            IssuePriorityDistributionChartDTO issueTypeDistributionChartDTO = toTarget(issuePriorityDistributionChartDO, IssuePriorityDistributionChartDTO.class);
            issueTypeDistributionChartDTO.setName(priorityDTOMap.get(issuePriorityDistributionChartDO.getPriorityId()).getName());
            issueTypeDistributionChartDTO.setPriorityDTO(priorityDTOMap.get(issuePriorityDistributionChartDO.getPriorityId()));
            issuePriorityDistributionChartDTOS.add(issueTypeDistributionChartDTO);
        });
        return issuePriorityDistributionChartDTOS;
    }
}
