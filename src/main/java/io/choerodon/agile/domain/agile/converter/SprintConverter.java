package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.infra.dataobject.SprintConvertDTO;
import io.choerodon.agile.infra.dataobject.SprintDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Component
public class SprintConverter {
    public SprintDTO entityToDo(SprintConvertDTO sprintConvertDTO){
        SprintDTO sprintDTO = new SprintDTO();
        BeanUtils.copyProperties(sprintConvertDTO, sprintDTO);
        return sprintDTO;
    }

    public SprintConvertDTO doToEntity(SprintDTO sprintDTO) {
        SprintConvertDTO sprintConvertDTO = new SprintConvertDTO();
        BeanUtils.copyProperties(sprintDTO, sprintConvertDTO);
        return sprintConvertDTO;
    }
}
