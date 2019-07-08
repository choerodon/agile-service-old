package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.StoryMapWidthDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
public interface StoryMapWidthService {

    StoryMapWidthDTO create(Long projectId, StoryMapWidthDTO storyMapWidthDTO);

    StoryMapWidthDTO update(Long projectId, StoryMapWidthDTO storyMapWidthDTO);
}
