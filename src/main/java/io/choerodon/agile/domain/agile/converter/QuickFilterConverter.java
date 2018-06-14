package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.QuickFilterDTO;
import io.choerodon.agile.domain.agile.entity.QuickFilterE;
import io.choerodon.agile.infra.dataobject.QuickFilterDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class QuickFilterConverter implements ConvertorI<QuickFilterE, QuickFilterDO, QuickFilterDTO> {

    @Override
    public QuickFilterE dtoToEntity(QuickFilterDTO quickFilterDTO) {
        QuickFilterE quickFilterE = new QuickFilterE();
        BeanUtils.copyProperties(quickFilterDTO, quickFilterE);
        return quickFilterE;
    }

    @Override
    public QuickFilterDTO entityToDto(QuickFilterE quickFilterE) {
        QuickFilterDTO quickFilterDTO = new QuickFilterDTO();
        BeanUtils.copyProperties(quickFilterE, quickFilterDTO);
        return quickFilterDTO;
    }

    @Override
    public QuickFilterE doToEntity(QuickFilterDO quickFilterDO) {
        QuickFilterE quickFilterE = new QuickFilterE();
        BeanUtils.copyProperties(quickFilterDO, quickFilterE);
        return quickFilterE;
    }

    @Override
    public QuickFilterDO entityToDo(QuickFilterE quickFilterE) {
        QuickFilterDO quickFilterDO = new QuickFilterDO();
        BeanUtils.copyProperties(quickFilterE, quickFilterDO);
        return quickFilterDO;
    }

    @Override
    public QuickFilterDTO doToDto(QuickFilterDO quickFilterDO) {
        QuickFilterDTO quickFilterDTO = new QuickFilterDTO();
        BeanUtils.copyProperties(quickFilterDO, quickFilterDTO);
        return quickFilterDTO;
    }

    @Override
    public QuickFilterDO dtoToDo(QuickFilterDTO quickFilterDTO) {
        QuickFilterDO quickFilterDO = new QuickFilterDO();
        BeanUtils.copyProperties(quickFilterDTO, quickFilterDO);
        return quickFilterDO;
    }
}
