package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.IssueLinkTypeCreateVO;
import io.choerodon.agile.api.vo.IssueLinkTypeVO;
import io.choerodon.agile.api.vo.IssueLinkTypeSearchVO;
import com.github.pagehelper.PageInfo;
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDTO;
import io.choerodon.base.domain.PageRequest;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public interface IssueLinkTypeService {

    /**
     * 查询issueLink类型
     *
     * @param projectId              projectId
     * @param issueLinkTypeId        issueLinkTypeId不包含的id
     * @param issueLinkTypeSearchVO issueLinkTypeSearchVO
     * @param pageRequest            pageRequest
     * @return IssueLinkTypeVO
     */
    PageInfo<IssueLinkTypeVO> listIssueLinkType(Long projectId, Long issueLinkTypeId, IssueLinkTypeSearchVO issueLinkTypeSearchVO, PageRequest pageRequest);

    /**
     * 创建issueLinkType
     *
     * @param issueLinkTypeCreateVO issueLinkTypeCreateVO
     * @return IssueLinkTypeVO
     */
    IssueLinkTypeVO createIssueLinkType(IssueLinkTypeCreateVO issueLinkTypeCreateVO);

    /**
     * 修改issueLinkType
     *
     * @param issueLinkTypeVO issueLinkTypeVO
     * @return IssueLinkTypeVO
     */
    IssueLinkTypeVO updateIssueLinkType(IssueLinkTypeVO issueLinkTypeVO);

    /**
     * 删除IssueLinkType
     *
     * @param issueLinkTypeId   issueLinkTypeId
     * @param toIssueLinkTypeId toIssueLinkTypeId（把这个issueLinkType下的链接转移到另外的issueLink，否则直接删除）
     * @param projectId         projectId
     * @return int
     */
    int deleteIssueLinkType(Long issueLinkTypeId, Long toIssueLinkTypeId, Long projectId);

    /**
     * 根据项目id和linkTypeId查询IssueLinkTypeDTO
     *
     * @param projectId  projectId
     * @param linkTypeId linkTypeId
     * @return IssueLinkTypeVO
     */
    IssueLinkTypeVO queryIssueLinkType(Long projectId, Long linkTypeId);

    /**
     * 创建项目初始化issueLinkType
     *
     * @param projectId projectId
     */
    void initIssueLinkType(Long projectId);

    boolean queryIssueLinkTypeName(Long projectId, String issueLinkTypeName, Long issueLinkTypeId);


    /**
     * 更新
     *
     * @param issueLinkTypeDTO issueLinkTypeDTO
     * @return IssueLinkTypeDTO
     */
    IssueLinkTypeDTO update(IssueLinkTypeDTO issueLinkTypeDTO);

    /**
     * 创建
     *
     * @param issueLinkTypeDTO issueLinkTypeDTO
     * @return IssueLinkTypeDTO
     */
    IssueLinkTypeDTO create(IssueLinkTypeDTO issueLinkTypeDTO);

    /**
     * 删除
     *
     * @param linkTypeId linkTypeId
     * @param projectId  projectId
     * @return int
     */
    int delete(Long linkTypeId, Long projectId);

    /**
     * 删除issue下的link关系
     *
     * @param issueLinkTypeId issueLinkTypeId
     * @return int
     */
    int deleteIssueLinkTypeRel(Long issueLinkTypeId);

    /**
     * 批量修改issue链接关系的类型到新的类型
     *
     * @param issueLinkTypeId   issueLinkTypeId
     * @param toIssueLinkTypeId toIssueLinkTypeId
     * @return int
     */
    int batchUpdateRelToIssueLinkType(Long issueLinkTypeId, Long toIssueLinkTypeId);
}
