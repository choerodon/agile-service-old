package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.WikiRelationDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
public interface WikiRelationService {

    void create(Long projectId, WikiRelationDTO wikiRelationDTO);

    List<WikiRelationDTO> queryByIssueId(Long projectId, Long issueId);

    void deleteById(Long projectId, Long id);
}
