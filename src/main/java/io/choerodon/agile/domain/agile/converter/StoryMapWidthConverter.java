package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.StoryMapWidthDTO;
import io.choerodon.agile.domain.agile.entity.StoryMapWidthE;
import io.choerodon.agile.infra.dataobject.StoryMapWidthDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StoryMapWidthConverter implements ConvertorI<StoryMapWidthE, StoryMapWidthDO, StoryMapWidthDTO> {

    @Override
    public StoryMapWidthE dtoToEntity(StoryMapWidthDTO storyMapWidthDTO) {
        StoryMapWidthE storyMapWidthE = new StoryMapWidthE();
        BeanUtils.copyProperties(storyMapWidthDTO, storyMapWidthE);
        return storyMapWidthE;
    }

    @Override
    public StoryMapWidthDTO entityToDto(StoryMapWidthE storyMapWidthE) {
        StoryMapWidthDTO storyMapWidthDTO = new StoryMapWidthDTO();
        BeanUtils.copyProperties(storyMapWidthE, storyMapWidthDTO);
        return storyMapWidthDTO;
    }

    @Override
    public StoryMapWidthE doToEntity(StoryMapWidthDO storyMapWidthDO) {
        StoryMapWidthE storyMapWidthE = new StoryMapWidthE();
        BeanUtils.copyProperties(storyMapWidthDO, storyMapWidthE);
        return storyMapWidthE;
    }

    @Override
    public StoryMapWidthDO entityToDo(StoryMapWidthE storyMapWidthE) {
        StoryMapWidthDO storyMapWidthDO = new StoryMapWidthDO();
        BeanUtils.copyProperties(storyMapWidthE, storyMapWidthDO);
        return storyMapWidthDO;
    }

    @Override
    public StoryMapWidthDTO doToDto(StoryMapWidthDO storyMapWidthDO) {
        StoryMapWidthDTO storyMapWidthDTO = new StoryMapWidthDTO();
        BeanUtils.copyProperties(storyMapWidthDO, storyMapWidthDTO);
        return storyMapWidthDTO;
    }

    @Override
    public StoryMapWidthDO dtoToDo(StoryMapWidthDTO storyMapWidthDTO) {
        StoryMapWidthDO storyMapWidthDO = new StoryMapWidthDO();
        BeanUtils.copyProperties(storyMapWidthDTO, storyMapWidthDO);
        return storyMapWidthDO;
    }
}
