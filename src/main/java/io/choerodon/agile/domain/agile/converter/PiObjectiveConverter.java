package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.PiObjectiveVO;
import io.choerodon.agile.domain.agile.entity.PiObjectiveE;
import io.choerodon.agile.infra.dataobject.PiObjectiveDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiObjectiveConverter implements ConvertorI<PiObjectiveE, PiObjectiveVO, PiObjectiveDTO> {

    @Override
    public PiObjectiveE dtoToEntity(PiObjectiveDTO piObjectiveDTO) {
        PiObjectiveE piObjectiveE = new PiObjectiveE();
        BeanUtils.copyProperties(piObjectiveDTO, piObjectiveE);
        return piObjectiveE;
    }

    @Override
    public PiObjectiveDTO entityToDto(PiObjectiveE piObjectiveE) {
        PiObjectiveDTO piObjectiveDTO = new PiObjectiveDTO();
        BeanUtils.copyProperties(piObjectiveE, piObjectiveDTO);
        return piObjectiveDTO;
    }

    @Override
    public PiObjectiveE doToEntity(PiObjectiveVO piObjectiveVO) {
        PiObjectiveE piObjectiveE = new PiObjectiveE();
        BeanUtils.copyProperties(piObjectiveVO, piObjectiveE);
        return piObjectiveE;
    }

    @Override
    public PiObjectiveVO entityToDo(PiObjectiveE piObjectiveE) {
        PiObjectiveVO piObjectiveVO = new PiObjectiveVO();
        BeanUtils.copyProperties(piObjectiveE, piObjectiveVO);
        return piObjectiveVO;
    }

    @Override
    public PiObjectiveDTO doToDto(PiObjectiveVO piObjectiveVO) {
        PiObjectiveDTO piObjectiveDTO = new PiObjectiveDTO();
        BeanUtils.copyProperties(piObjectiveVO, piObjectiveDTO);
        return piObjectiveDTO;
    }

    @Override
    public PiObjectiveVO dtoToDo(PiObjectiveDTO piObjectiveDTO) {
        PiObjectiveVO piObjectiveVO = new PiObjectiveVO();
        BeanUtils.copyProperties(piObjectiveDTO, piObjectiveVO);
        return piObjectiveVO;
    }
}
