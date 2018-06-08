package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.SprintCompleteMessageDTO;
import io.choerodon.agile.infra.dataobject.SprintCompleteMessageDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/17.
 */
@Component
public class SprintCompleteMessageAssembler {
    public SprintCompleteMessageDTO doToDTO(SprintCompleteMessageDO sprintCompleteMessageDO){
        SprintCompleteMessageDTO sprintCompleteMessageDTO = new SprintCompleteMessageDTO();
        BeanUtils.copyProperties(sprintCompleteMessageDO, sprintCompleteMessageDTO);
        return sprintCompleteMessageDTO;
    }
}
