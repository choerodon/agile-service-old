package io.choerodon.agile.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.agile.api.vo.IssueTypeSearchVO;
import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.IssueTypeWithInfoVO;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/8
 */
public interface IssueTypeService {


    IssueTypeVO queryById(Long organizationId, Long issueTypeId);

    IssueTypeVO create(Long organizationId, IssueTypeVO issueTypeVO);

    IssueTypeVO update(IssueTypeVO issueTypeVO);

    Boolean delete(Long organizationId, Long issueTypeId);

    Map<String, Object> checkDelete(Long organizationId, Long issueTypeId);

    PageInfo<IssueTypeWithInfoVO> queryIssueTypeList(PageRequest pageRequest, Long organizationId, IssueTypeSearchVO issueTypeSearchVO);

    Boolean checkName(Long organizationId, String name, Long id);

    List<IssueTypeVO> queryByOrgId(Long organizationId);

    /**
     * 通过状态机方案id查询当前组织下的问题类型（包含对应的状态机）
     *
     * @param organizationId 组织id
     * @return 问题类型列表
     */
    List<IssueTypeVO> queryIssueTypeByStateMachineSchemeId(Long organizationId, Long schemeId);

    /**
     * 消费组织创建事件生成组织初始化的五种issue类型
     *
     * @param organizationId organizationId
     */
    void initIssueTypeByConsumeCreateOrganization(Long organizationId);

    Map<Long, IssueTypeVO> listIssueTypeMap(Long organizationId);

    Map<Long, Map<String, Long>> initIssueTypeData(Long organizationId, List<Long> orgIds);
}
