package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.StoryMapWidthE;
import io.choerodon.agile.infra.dataobject.StoryMapWidthDO;
import io.choerodon.agile.infra.mapper.StoryMapWidthMapper;
import io.choerodon.agile.infra.repository.StoryMapWidthRepository;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StoryMapWidthRepositoryImpl implements StoryMapWidthRepository {

    @Autowired
    private StoryMapWidthMapper storyMapWidthMapper;

    @Override
    public StoryMapWidthE create(StoryMapWidthE storyMapWidthE) {
        StoryMapWidthDO storyMapWidthDO = ConvertHelper.convert(storyMapWidthE, StoryMapWidthDO.class);
        if (storyMapWidthMapper.insert(storyMapWidthDO) != 1) {
            throw new CommonException("error.storyMapWidthDO.insert");
        }
        return ConvertHelper.convert(storyMapWidthMapper.selectByPrimaryKey(storyMapWidthDO.getId()), StoryMapWidthE.class);
    }

    @Override
    public StoryMapWidthE updateBySelective(StoryMapWidthE storyMapWidthE) {
        StoryMapWidthDO storyMapWidthDO = ConvertHelper.convert(storyMapWidthE, StoryMapWidthDO.class);
        if (storyMapWidthMapper.updateByPrimaryKeySelective(storyMapWidthDO) != 1) {
            throw new CommonException("error.storyMapWidthDO.insert");
        }
        return ConvertHelper.convert(storyMapWidthMapper.selectByPrimaryKey(storyMapWidthDO.getId()), StoryMapWidthE.class);
    }


}
