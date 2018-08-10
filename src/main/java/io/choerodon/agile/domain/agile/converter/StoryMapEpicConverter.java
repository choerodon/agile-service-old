package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.StoryMapEpicDTO;
import io.choerodon.agile.infra.dataobject.StoryMapEpicDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/10.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StoryMapEpicConverter implements ConvertorI<Object, StoryMapEpicDO, StoryMapEpicDTO> {

    @Override
    public StoryMapEpicDTO doToDto(StoryMapEpicDO storyMapEpicDO) {
        StoryMapEpicDTO storyMapEpicDTO = new StoryMapEpicDTO();
        BeanUtils.copyProperties(storyMapEpicDO, storyMapEpicDTO);
        return storyMapEpicDTO;
    }
}
