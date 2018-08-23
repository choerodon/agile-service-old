package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.SprintUnClosedDTO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SprintUnClosedConverter implements ConvertorI<Object, SprintDO, SprintUnClosedDTO> {

    @Override
    public SprintUnClosedDTO doToDto(SprintDO sprintDO) {
        SprintUnClosedDTO sprintUnClosedDTO = new SprintUnClosedDTO();
        BeanUtils.copyProperties(sprintDO, sprintUnClosedDTO);
        return sprintUnClosedDTO;
    }

}
