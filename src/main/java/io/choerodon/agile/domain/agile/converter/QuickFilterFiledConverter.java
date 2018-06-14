package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.QuickFilterFiledDTO;
import io.choerodon.agile.domain.agile.entity.QuickFilterFiledE;
import io.choerodon.agile.infra.dataobject.QuickFilterFiledDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class QuickFilterFiledConverter implements ConvertorI<QuickFilterFiledE, QuickFilterFiledDO, QuickFilterFiledDTO> {

    @Override
    public QuickFilterFiledE dtoToEntity(QuickFilterFiledDTO quickFilterFiledDTO) {
        QuickFilterFiledE quickFilterFiledE = new QuickFilterFiledE();
        BeanUtils.copyProperties(quickFilterFiledDTO, quickFilterFiledE);
        return quickFilterFiledE;
    }

    @Override
    public QuickFilterFiledDTO entityToDto(QuickFilterFiledE quickFilterFiledE) {
        QuickFilterFiledDTO quickFilterFiledDTO = new QuickFilterFiledDTO();
        BeanUtils.copyProperties(quickFilterFiledE, quickFilterFiledDTO);
        return quickFilterFiledDTO;
    }

    @Override
    public QuickFilterFiledE doToEntity(QuickFilterFiledDO quickFilterFiledDO) {
        QuickFilterFiledE quickFilterFiledE = new QuickFilterFiledE();
        BeanUtils.copyProperties(quickFilterFiledDO, quickFilterFiledE);
        return quickFilterFiledE;
    }

    @Override
    public QuickFilterFiledDO entityToDo(QuickFilterFiledE quickFilterFiledE) {
        QuickFilterFiledDO quickFilterFiledDO = new QuickFilterFiledDO();
        BeanUtils.copyProperties(quickFilterFiledE, quickFilterFiledDO);
        return quickFilterFiledDO;
    }

    @Override
    public QuickFilterFiledDTO doToDto(QuickFilterFiledDO quickFilterFiledDO) {
        QuickFilterFiledDTO quickFilterFiledDTO = new QuickFilterFiledDTO();
        BeanUtils.copyProperties(quickFilterFiledDO, quickFilterFiledDTO);
        return quickFilterFiledDTO;
    }

    @Override
    public QuickFilterFiledDO dtoToDo(QuickFilterFiledDTO quickFilterFiledDTO) {
        QuickFilterFiledDO quickFilterFiledDO = new QuickFilterFiledDO();
        BeanUtils.copyProperties(quickFilterFiledDTO, quickFilterFiledDO);
        return quickFilterFiledDO;
    }
}
