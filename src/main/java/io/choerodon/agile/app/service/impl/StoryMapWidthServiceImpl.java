package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.StoryMapWidthVO;
import io.choerodon.agile.api.validator.StoryMapWidthValidator;
import io.choerodon.agile.app.service.StoryMapWidthService;
import io.choerodon.agile.infra.dataobject.StoryMapWidthDTO;
import io.choerodon.agile.infra.mapper.StoryMapWidthMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class StoryMapWidthServiceImpl implements StoryMapWidthService {

    @Autowired
    private StoryMapWidthValidator storyMapWidthValidator;

    @Autowired
    private StoryMapWidthMapper storyMapWidthMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public StoryMapWidthVO create(Long projectId, StoryMapWidthVO storyMapWidthVO) {
        storyMapWidthValidator.checkStoryMapWidthCreate(storyMapWidthVO);
        StoryMapWidthDTO storyMapWidthDTO = modelMapper.map(storyMapWidthVO, StoryMapWidthDTO.class);
        if (storyMapWidthMapper.insert(storyMapWidthDTO) != 1) {
            throw new CommonException("error.storyMapWidthDTO.insert");
        }
        return modelMapper.map(storyMapWidthMapper.selectByPrimaryKey(storyMapWidthDTO.getId()), StoryMapWidthVO.class);
    }

    @Override
    public StoryMapWidthVO update(Long projectId, StoryMapWidthVO storyMapWidthVO) {
        storyMapWidthValidator.checkStoryMapWidthUpdate(storyMapWidthVO);
        StoryMapWidthDTO storyMapWidthDTO = modelMapper.map(storyMapWidthVO, StoryMapWidthDTO.class);
        if (storyMapWidthMapper.updateByPrimaryKeySelective(storyMapWidthDTO) != 1) {
            throw new CommonException("error.storyMapWidthDTO.update");
        }
        return modelMapper.map(storyMapWidthMapper.selectByPrimaryKey(storyMapWidthDTO.getId()), StoryMapWidthVO.class);
    }

}
