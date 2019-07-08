package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.StoryMapWidthDTO;
import io.choerodon.agile.api.validator.StoryMapWidthValidator;
import io.choerodon.agile.app.service.StoryMapWidthService;
import io.choerodon.agile.domain.agile.entity.StoryMapWidthE;
import io.choerodon.agile.infra.repository.StoryMapWidthRepository;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class StoryMapWidthServiceImpl implements StoryMapWidthService {

    @Autowired
    private StoryMapWidthRepository storyMapWidthRepository;

    @Autowired
    private StoryMapWidthValidator storyMapWidthValidator;

    @Override
    public StoryMapWidthDTO create(Long projectId, StoryMapWidthDTO storyMapWidthDTO) {
        storyMapWidthValidator.checkStoryMapWidthCreate(storyMapWidthDTO);
        StoryMapWidthE storyMapWidthE = storyMapWidthRepository.create(ConvertHelper.convert(storyMapWidthDTO, StoryMapWidthE.class));
        return ConvertHelper.convert(storyMapWidthE, StoryMapWidthDTO.class);
    }

    @Override
    public StoryMapWidthDTO update(Long projectId, StoryMapWidthDTO storyMapWidthDTO) {
        storyMapWidthValidator.checkStoryMapWidthUpdate(storyMapWidthDTO);
        StoryMapWidthE storyMapWidthE = storyMapWidthRepository.updateBySelective(ConvertHelper.convert(storyMapWidthDTO, StoryMapWidthE.class));
        return ConvertHelper.convert(storyMapWidthE, StoryMapWidthDTO.class);
    }

}
