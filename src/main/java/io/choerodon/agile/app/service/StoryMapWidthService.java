package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.StoryMapWidthVO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
public interface StoryMapWidthService {

    StoryMapWidthVO create(Long projectId, StoryMapWidthVO storyMapWidthVO);

    StoryMapWidthVO update(Long projectId, StoryMapWidthVO storyMapWidthVO);
}
