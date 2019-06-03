package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.StoryMapDragDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/5/31.
 * Email: fuqianghuang01@gmail.com
 */
public interface StoryMapService {

    JSONObject queryStoryMap(Long projectId, Long organizationId);

    void storyMapMove(Long projectId, StoryMapDragDTO storyMapDragDTO);

}
