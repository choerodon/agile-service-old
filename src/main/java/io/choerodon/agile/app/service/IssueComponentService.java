package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.ComponentForListDTO;
import io.choerodon.agile.api.dto.IssueComponentDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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

    Page<ComponentForListDTO> queryComponentByProjectId(Long projectId, Long componentId, Boolean noIssueTest, SearchDTO searchDTO, PageRequest pageRequest);

    List<IssueDTO> queryIssuesByComponentId(Long projectId, Long componentId);

    List<ComponentForListDTO> listByProjectIdForTest(Long projectId, Long componentId, Boolean noIssueTest);
}
