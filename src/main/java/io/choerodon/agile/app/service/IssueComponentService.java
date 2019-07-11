package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.ComponentForListVO;
import io.choerodon.agile.api.vo.IssueComponentVO;
import io.choerodon.agile.api.vo.IssueVO;
import io.choerodon.agile.api.vo.SearchVO;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */

public interface IssueComponentService {

    IssueComponentVO create(Long projectId, IssueComponentVO issueComponentVO);

    IssueComponentVO update(Long projectId, Long id, IssueComponentVO issueComponentVO);

    void delete(Long projectId, Long id, Long relateComponentId);

    IssueComponentVO queryComponentsById(Long projectId, Long id);

    PageInfo<ComponentForListVO> queryComponentByProjectId(Long projectId, Long componentId, Boolean noIssueTest, SearchVO searchVO, PageRequest pageRequest);

    List<IssueVO> queryIssuesByComponentId(Long projectId, Long componentId);

    List<ComponentForListVO> listByProjectIdForTest(Long projectId, Long componentId, Boolean noIssueTest);

    Boolean checkComponentName(Long projectId, String componentName);
}
