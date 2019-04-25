package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.SprintCalendarDTO;
import io.choerodon.agile.infra.dataobject.SprintCalendarDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/25.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class SprintCalendarConverter implements ConvertorI<Object, SprintCalendarDO, SprintCalendarDTO> {

    @Override
    public SprintCalendarDTO doToDto(SprintCalendarDO sprintCalendarDO) {
        SprintCalendarDTO sprintCalendarDTO = new SprintCalendarDTO();
        BeanUtils.copyProperties(sprintCalendarDO, sprintCalendarDTO);
        return sprintCalendarDTO;
    }
}
