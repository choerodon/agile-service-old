package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.SprintDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Component
public class SprintConverter {
    public SprintDO entityToDo(SprintE sprintE){
        SprintDO sprintDO = new SprintDO();
        BeanUtils.copyProperties(sprintE, sprintDO);
        return sprintDO;
    }

    public SprintE doToEntity(SprintDO sprintDO) {
        SprintE sprintE = new SprintE();
        BeanUtils.copyProperties(sprintDO, sprintE);
        return sprintE;
    }
}
