package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.SearchVO;
import io.choerodon.agile.api.vo.StoryMapDragVO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/5/31.
 * Email: fuqianghuang01@gmail.com
 */
public interface StoryMapService {

    JSONObject queryStoryMap(Long projectId, Long organizationId, SearchVO searchVO);

    JSONObject queryStoryMapDemand(Long projectId, SearchVO searchVO);

    void storyMapMove(Long projectId, StoryMapDragVO storyMapDragVO);

}
