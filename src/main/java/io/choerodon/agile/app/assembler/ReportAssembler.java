package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.infra.common.utils.ColorUtil;
import io.choerodon.agile.infra.dataobject.ColumnDO;
import io.choerodon.agile.infra.dataobject.IssueBurnDownReportDO;
import io.choerodon.agile.infra.dataobject.LookupValueDO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.mapper.LookupValueMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
@Component
public class ReportAssembler extends AbstractAssembler {

    @Autowired
    private LookupValueMapper lookupValueMapper;

    private static final String ISSUE_STATUS_COLOR = "issue_status_color";

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

    public List<IssueBurnDownReportDTO> issueBurnDownReportDoToDto(List<IssueBurnDownReportDO> issueBurnDownReportDOS, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap) {
        List<IssueBurnDownReportDTO> issueBurnDownReportDTOS = new ArrayList<>(issueBurnDownReportDOS.size());
        if(!issueBurnDownReportDOS.isEmpty()){
            LookupValueDO lookupValueDO = new LookupValueDO();
            lookupValueDO.setTypeCode(ISSUE_STATUS_COLOR);
            Map<String, String> lookupValueMap = lookupValueMapper.select(lookupValueDO).stream().collect(Collectors.toMap(LookupValueDO::getValueCode, LookupValueDO::getName));
            issueBurnDownReportDOS.forEach(issueBurnDownReportDO -> {
                IssueBurnDownReportDTO issueBurnDownReportDTO = new IssueBurnDownReportDTO();
                BeanUtils.copyProperties(issueBurnDownReportDO,issueBurnDownReportDTO);
                issueBurnDownReportDTO.setPriorityDTO(priorityMap.get(issueBurnDownReportDO.getPriorityId()));
//                issueBurnDownReportDTO.setStatusColor(ColorUtil.initializationStatusColor(issueBurnDownReportDTO.getStatusCode(), lookupValueMap));
                issueBurnDownReportDTO.setStatusMapDTO(statusMapDTOMap.get(issueBurnDownReportDO.getStatusId()));
                issueBurnDownReportDTOS.add(issueBurnDownReportDTO);
            });
        }
        return issueBurnDownReportDTOS;
    }
}
