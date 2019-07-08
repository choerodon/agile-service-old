package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.SprintCalendarVO;
import io.choerodon.agile.infra.dataobject.SprintCalendarDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/25.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class SprintCalendarConverter implements ConvertorI<Object, SprintCalendarDTO, SprintCalendarVO> {

    @Override
    public SprintCalendarVO doToDto(SprintCalendarDTO sprintCalendarDTO) {
        SprintCalendarVO sprintCalendarVO = new SprintCalendarVO();
        BeanUtils.copyProperties(sprintCalendarDTO, sprintCalendarVO);
        return sprintCalendarVO;
    }
}
