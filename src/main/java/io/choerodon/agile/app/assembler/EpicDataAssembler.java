package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.EpicDataDTO;
import io.choerodon.agile.infra.dataobject.EpicDataDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/16.
 */
@Component
public class EpicDataAssembler {
    public EpicDataDTO doToEntity(EpicDataDO epicDataDO){
        EpicDataDTO epicDataDTO =  new EpicDataDTO();
        BeanUtils.copyProperties(epicDataDO, epicDataDTO);
        return epicDataDTO;
    }

    public List<EpicDataDTO> doListToDTO(List<EpicDataDO> epicDataDOList){
        List<EpicDataDTO> epicDataDTOList = new ArrayList<>();
        epicDataDOList.forEach(epicDataDO -> epicDataDTOList.add(doToEntity(epicDataDO)));
        return epicDataDTOList;
    }
}
