package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.WikiRelationVO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
public interface WikiRelationService {

    void create(Long projectId, List<WikiRelationVO> wikiRelationVOList);

    JSONObject queryByIssueId(Long projectId, Long issueId);

    void deleteById(Long projectId, Long id);

    void moveWikiRelation();
}
