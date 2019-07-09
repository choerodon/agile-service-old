package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.SprintDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Component
public class SprintConverter {
    public SprintDTO entityToDo(SprintE sprintE){
        SprintDTO sprintDTO = new SprintDTO();
        BeanUtils.copyProperties(sprintE, sprintDTO);
        return sprintDTO;
    }

    public SprintE doToEntity(SprintDTO sprintDTO) {
        SprintE sprintE = new SprintE();
        BeanUtils.copyProperties(sprintDTO, sprintE);
        return sprintE;
    }
}
