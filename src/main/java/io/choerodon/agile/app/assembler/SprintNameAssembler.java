package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.SprintNameDTO;
import io.choerodon.agile.infra.dataobject.SprintNameDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/17.
 */
@Component
public class SprintNameAssembler {
    public SprintNameDTO doToDTO(SprintNameDO sprintNameDO){
        if(sprintNameDO == null){
            return null;
        }
        SprintNameDTO sprintNameDTO = new SprintNameDTO();
        BeanUtils.copyProperties(sprintNameDO, sprintNameDTO);
        return sprintNameDTO;
    }

    public List<SprintNameDTO> doListToDTO(List<SprintNameDO> sprintNameDOS){
        List<SprintNameDTO> sprintNameDTOList = new ArrayList<>();
        sprintNameDOS.forEach(sprintNameDO -> sprintNameDTOList.add(doToDTO(sprintNameDO)));
        return sprintNameDTOList;
    }
}
