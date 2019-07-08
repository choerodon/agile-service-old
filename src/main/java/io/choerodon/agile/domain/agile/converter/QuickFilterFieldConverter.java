package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.QuickFilterFieldDTO;
import io.choerodon.agile.domain.agile.entity.QuickFilterFieldE;
import io.choerodon.agile.infra.dataobject.QuickFilterFieldDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class QuickFilterFieldConverter implements ConvertorI<QuickFilterFieldE, QuickFilterFieldDO, QuickFilterFieldDTO> {

    @Override
    public QuickFilterFieldE dtoToEntity(QuickFilterFieldDTO quickFilterFieldDTO) {
        QuickFilterFieldE quickFilterFieldE = new QuickFilterFieldE();
        BeanUtils.copyProperties(quickFilterFieldDTO, quickFilterFieldE);
        return quickFilterFieldE;
    }

    @Override
    public QuickFilterFieldDTO entityToDto(QuickFilterFieldE quickFilterFieldE) {
        QuickFilterFieldDTO quickFilterFieldDTO = new QuickFilterFieldDTO();
        BeanUtils.copyProperties(quickFilterFieldE, quickFilterFieldDTO);
        return quickFilterFieldDTO;
    }

    @Override
    public QuickFilterFieldE doToEntity(QuickFilterFieldDO quickFilterFieldDO) {
        QuickFilterFieldE quickFilterFieldE = new QuickFilterFieldE();
        BeanUtils.copyProperties(quickFilterFieldDO, quickFilterFieldE);
        return quickFilterFieldE;
    }

    @Override
    public QuickFilterFieldDO entityToDo(QuickFilterFieldE quickFilterFieldE) {
        QuickFilterFieldDO quickFilterFieldDO = new QuickFilterFieldDO();
        BeanUtils.copyProperties(quickFilterFieldE, quickFilterFieldDO);
        return quickFilterFieldDO;
    }

    @Override
    public QuickFilterFieldDTO doToDto(QuickFilterFieldDO quickFilterFieldDO) {
        QuickFilterFieldDTO quickFilterFieldDTO = new QuickFilterFieldDTO();
        BeanUtils.copyProperties(quickFilterFieldDO, quickFilterFieldDTO);
        return quickFilterFieldDTO;
    }

    @Override
    public QuickFilterFieldDO dtoToDo(QuickFilterFieldDTO quickFilterFieldDTO) {
        QuickFilterFieldDO quickFilterFieldDO = new QuickFilterFieldDO();
        BeanUtils.copyProperties(quickFilterFieldDTO, quickFilterFieldDO);
        return quickFilterFieldDO;
    }
}
