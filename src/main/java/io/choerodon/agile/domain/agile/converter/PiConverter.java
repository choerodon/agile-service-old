package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.PiDTO;
import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.infra.dataobject.PiDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiConverter implements ConvertorI<PiE, PiDTO, PiDO> {

    @Override
    public PiE dtoToEntity(PiDO piDO) {
        PiE piE = new PiE();
        BeanUtils.copyProperties(piDO, piE);
        return piE;
    }

    @Override
    public PiDO entityToDto(PiE piE) {
        PiDO piDO = new PiDO();
        BeanUtils.copyProperties(piE, piDO);
        return piDO;
    }

    @Override
    public PiE doToEntity(PiDTO piDTO) {
        PiE piE = new PiE();
        BeanUtils.copyProperties(piDTO, piE);
        return piE;
    }

    @Override
    public PiDTO entityToDo(PiE piE) {
        PiDTO piDTO = new PiDTO();
        BeanUtils.copyProperties(piE, piDTO);
        return piDTO;
    }

    @Override
    public PiDO doToDto(PiDTO piDTO) {
        PiDO piDO = new PiDO();
        BeanUtils.copyProperties(piDTO, piDO);
        return piDO;
    }

    @Override
    public PiDTO dtoToDo(PiDO piDO) {
        PiDTO piDTO = new PiDTO();
        BeanUtils.copyProperties(piDO, piDTO);
        return piDTO;
    }
}
