package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.ColumnChangeDTO;
import io.choerodon.agile.api.dto.ColumnDTO;
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

    public List<ColumnDTO> columnListDoToDto(List<ColumnDO> columnDOList) {
        List<ColumnDTO> columnDTOList = new ArrayList<>();
        columnDOList.forEach(columnDO -> {
            ColumnDTO columnDTO = new ColumnDTO();
            BeanUtils.copyProperties(columnDO, columnDTO);
            columnDTOList.add(columnDTO);
        });
        return columnDTOList;
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
