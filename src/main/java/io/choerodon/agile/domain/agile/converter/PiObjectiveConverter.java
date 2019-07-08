package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.PiObjectiveDTO;
import io.choerodon.agile.domain.agile.entity.PiObjectiveE;
import io.choerodon.agile.infra.dataobject.PiObjectiveDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiObjectiveConverter implements ConvertorI<PiObjectiveE, PiObjectiveDTO, PiObjectiveDO> {

    @Override
    public PiObjectiveE dtoToEntity(PiObjectiveDO piObjectiveDO) {
        PiObjectiveE piObjectiveE = new PiObjectiveE();
        BeanUtils.copyProperties(piObjectiveDO, piObjectiveE);
        return piObjectiveE;
    }

    @Override
    public PiObjectiveDO entityToDto(PiObjectiveE piObjectiveE) {
        PiObjectiveDO piObjectiveDO = new PiObjectiveDO();
        BeanUtils.copyProperties(piObjectiveE, piObjectiveDO);
        return piObjectiveDO;
    }

    @Override
    public PiObjectiveE doToEntity(PiObjectiveDTO piObjectiveDTO) {
        PiObjectiveE piObjectiveE = new PiObjectiveE();
        BeanUtils.copyProperties(piObjectiveDTO, piObjectiveE);
        return piObjectiveE;
    }

    @Override
    public PiObjectiveDTO entityToDo(PiObjectiveE piObjectiveE) {
        PiObjectiveDTO piObjectiveDTO = new PiObjectiveDTO();
        BeanUtils.copyProperties(piObjectiveE, piObjectiveDTO);
        return piObjectiveDTO;
    }

    @Override
    public PiObjectiveDO doToDto(PiObjectiveDTO piObjectiveDTO) {
        PiObjectiveDO piObjectiveDO = new PiObjectiveDO();
        BeanUtils.copyProperties(piObjectiveDTO, piObjectiveDO);
        return piObjectiveDO;
    }

    @Override
    public PiObjectiveDTO dtoToDo(PiObjectiveDO piObjectiveDO) {
        PiObjectiveDTO piObjectiveDTO = new PiObjectiveDTO();
        BeanUtils.copyProperties(piObjectiveDO, piObjectiveDTO);
        return piObjectiveDTO;
    }
}
