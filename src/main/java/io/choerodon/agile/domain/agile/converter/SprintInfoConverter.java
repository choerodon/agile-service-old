package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.dto.SprintInfoDTO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class SprintInfoConverter implements ConvertorI<Object, SprintDO, SprintInfoDTO> {

    @Override
    public SprintInfoDTO doToDto(SprintDO sprintDO) {
        SprintInfoDTO sprintInfoDTO = new SprintInfoDTO();
        BeanUtils.copyProperties(sprintDO, sprintInfoDTO);
        return sprintInfoDTO;
    }
}
