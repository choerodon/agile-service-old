package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.ColumnChangeDTO;
import io.choerodon.agile.api.dto.CumulativeFlowDiagramDTO;
import io.choerodon.agile.infra.dataobject.ColumnChangeDO;
import io.choerodon.agile.infra.dataobject.ColumnDO;
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
}
