package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.SprintUnClosedDTO;
import io.choerodon.agile.infra.dataobject.SprintDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SprintUnClosedConverter implements ConvertorI<Object, SprintDTO, SprintUnClosedDTO> {

    @Override
    public SprintUnClosedDTO doToDto(SprintDTO sprintDTO) {
        SprintUnClosedDTO sprintUnClosedDTO = new SprintUnClosedDTO();
        BeanUtils.copyProperties(sprintDTO, sprintUnClosedDTO);
        return sprintUnClosedDTO;
    }

}
