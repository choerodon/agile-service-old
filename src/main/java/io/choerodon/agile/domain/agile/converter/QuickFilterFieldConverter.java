package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.QuickFilterFieldVO;
import io.choerodon.agile.domain.agile.entity.QuickFilterFieldE;
import io.choerodon.agile.infra.dataobject.QuickFilterFieldDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class QuickFilterFieldConverter implements ConvertorI<QuickFilterFieldE, QuickFilterFieldDTO, QuickFilterFieldVO> {

    @Override
    public QuickFilterFieldE dtoToEntity(QuickFilterFieldVO quickFilterFieldVO) {
        QuickFilterFieldE quickFilterFieldE = new QuickFilterFieldE();
        BeanUtils.copyProperties(quickFilterFieldVO, quickFilterFieldE);
        return quickFilterFieldE;
    }

    @Override
    public QuickFilterFieldVO entityToDto(QuickFilterFieldE quickFilterFieldE) {
        QuickFilterFieldVO quickFilterFieldVO = new QuickFilterFieldVO();
        BeanUtils.copyProperties(quickFilterFieldE, quickFilterFieldVO);
        return quickFilterFieldVO;
    }

    @Override
    public QuickFilterFieldE doToEntity(QuickFilterFieldDTO quickFilterFieldDTO) {
        QuickFilterFieldE quickFilterFieldE = new QuickFilterFieldE();
        BeanUtils.copyProperties(quickFilterFieldDTO, quickFilterFieldE);
        return quickFilterFieldE;
    }

    @Override
    public QuickFilterFieldDTO entityToDo(QuickFilterFieldE quickFilterFieldE) {
        QuickFilterFieldDTO quickFilterFieldDTO = new QuickFilterFieldDTO();
        BeanUtils.copyProperties(quickFilterFieldE, quickFilterFieldDTO);
        return quickFilterFieldDTO;
    }

    @Override
    public QuickFilterFieldVO doToDto(QuickFilterFieldDTO quickFilterFieldDTO) {
        QuickFilterFieldVO quickFilterFieldVO = new QuickFilterFieldVO();
        BeanUtils.copyProperties(quickFilterFieldDTO, quickFilterFieldVO);
        return quickFilterFieldVO;
    }

    @Override
    public QuickFilterFieldDTO dtoToDo(QuickFilterFieldVO quickFilterFieldVO) {
        QuickFilterFieldDTO quickFilterFieldDTO = new QuickFilterFieldDTO();
        BeanUtils.copyProperties(quickFilterFieldVO, quickFilterFieldDTO);
        return quickFilterFieldDTO;
    }
}
