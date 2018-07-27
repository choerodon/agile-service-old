package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.ColumnChangeDTO;
import io.choerodon.agile.api.dto.CumulativeFlowDiagramDTO;
import io.choerodon.agile.api.dto.PieChartDTO;
import io.choerodon.agile.infra.dataobject.ColumnChangeDO;
import io.choerodon.agile.infra.dataobject.ColumnDO;
import io.choerodon.agile.infra.dataobject.PieChartDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
@Component
public class ReportAssembler {

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

    public List<ColumnChangeDTO> columnChangeListDoToDto(List<ColumnChangeDO> columnChangeDOList) {
        List<ColumnChangeDTO> columnChangeDTOList = new ArrayList<>();
        columnChangeDOList.forEach(columnChangeDO -> {
            ColumnChangeDTO columnChangeDTO = new ColumnChangeDTO();
            BeanUtils.copyProperties(columnChangeDO, columnChangeDTO);
            columnChangeDTOList.add(columnChangeDTO);
        });
        return columnChangeDTOList;
    }

    public List<PieChartDTO> pieChartDoToDto(List<PieChartDO> pieChartDOS) {
        List<PieChartDTO> pieChartDTOS = new ArrayList<>();
        pieChartDOS.parallelStream().forEach(pieChartDO -> {
            PieChartDTO pieChartDTO = new PieChartDTO();
            BeanUtils.copyProperties(pieChartDO,pieChartDTO);
            pieChartDTOS.add(pieChartDTO);
        });
        return pieChartDTOS;
    }
}
