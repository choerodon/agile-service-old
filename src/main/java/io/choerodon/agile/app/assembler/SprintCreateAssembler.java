package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.SprintDetailDTO;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.SprintDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class SprintCreateAssembler {
    public SprintDetailDTO entityToDto(SprintE sprintE) {
        SprintDetailDTO sprintDetailDTO = new SprintDetailDTO();
        BeanUtils.copyProperties(sprintE, sprintDetailDTO);
        return sprintDetailDTO;
    }

    public SprintE doToEntity(SprintDO sprintDO) {
        SprintE sprintE = new SprintE();
        BeanUtils.copyProperties(sprintDO, sprintE);
        return sprintE;
    }
}
