package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.ComponentForListDTO;
import io.choerodon.agile.api.vo.IssueComponentDTO;
import io.choerodon.agile.api.vo.IssueVO;
import io.choerodon.agile.api.vo.SearchDTO;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

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

    PageInfo<ComponentForListDTO> queryComponentByProjectId(Long projectId, Long componentId, Boolean noIssueTest, SearchDTO searchDTO, PageRequest pageRequest);

    List<IssueVO> queryIssuesByComponentId(Long projectId, Long componentId);

    List<ComponentForListDTO> listByProjectIdForTest(Long projectId, Long componentId, Boolean noIssueTest);

    Boolean checkComponentName(Long projectId, String componentName);
}
