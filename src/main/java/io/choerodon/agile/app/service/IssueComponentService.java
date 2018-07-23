package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.ComponentForListDTO;
import io.choerodon.agile.api.dto.IssueComponentDTO;
import io.choerodon.agile.api.dto.IssueDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */

public interface IssueComponentService {

    IssueComponentDTO create(Long projectId, IssueComponentDTO issueComponentDTO);

    IssueComponentDTO update(Long projectId, Long id, IssueComponentDTO issueComponentDTO);

    void delete(Long projectId, Long id, Long relateComponentId);

    IssueComponentDTO queryComponentsById(Long projectId, Long id);

    List<ComponentForListDTO> queryComponentByProjectId(Long projectId, Long componentId,Boolean noIssueTest);

    List<IssueDTO> queryIssuesByComponentId(Long projectId, Long componentId);
}
