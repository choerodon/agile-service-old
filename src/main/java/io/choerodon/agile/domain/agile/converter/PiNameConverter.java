package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.PiNameVO;
import io.choerodon.agile.infra.dataobject.PiNameDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiNameConverter implements ConvertorI<Object, PiNameDTO, PiNameVO> {

    @Override
    public PiNameVO doToDto(PiNameDTO piNameDTO) {
        PiNameVO piNameVO = new PiNameVO();
        BeanUtils.copyProperties(piNameDTO, piNameVO);
        return piNameVO;
    }
}
