package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueMoveDTO;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueDOToMoveDTOConverter implements ConvertorI<Object, IssueDO, IssueMoveDTO> {

    @Override
    public IssueMoveDTO doToDto(IssueDO issueDO) {
        IssueMoveDTO issueMoveDTO = new IssueMoveDTO();
        BeanUtils.copyProperties(issueDO, issueMoveDTO);
        return issueMoveDTO;
    }
}
