package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.CumulativeFlowDiagramDTO;
import io.choerodon.agile.api.dto.SprintBurnDownReportDTO;
import io.choerodon.agile.infra.dataobject.ColumnDO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
@Component
public class ReportAssembler extends AbstractAssembler {

    public List<CumulativeFlowDiagramDTO> columnListDoToDto(List<ColumnDO> columnDOList) {
        List<CumulativeFlowDiagramDTO> cumulativeFlowDiagramDTOList = new ArrayList<>();
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
}
