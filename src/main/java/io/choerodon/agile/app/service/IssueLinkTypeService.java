package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO;
import io.choerodon.agile.api.dto.IssueLinkTypeDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public interface IssueLinkTypeService {

    /**
     * 查询issueLink类型
     *
     * @param projectId       projectId
     * @param issueLinkTypeId issueLinkTypeId不包含的id
     * @param linkName        linkName
     * @param content         content
     * @param pageRequest     pageRequest
     * @return IssueLinkTypeDTO
     */
    Page<IssueLinkTypeDTO> listIssueLinkType(Long projectId, Long issueLinkTypeId, String linkName, String content, PageRequest pageRequest);

    /**
     * 创建issueLinkType
     *
     * @param issueLinkTypeCreateDTO issueLinkTypeCreateDTO
     * @return IssueLinkTypeDTO
     */
    IssueLinkTypeDTO createIssueLinkType(IssueLinkTypeCreateDTO issueLinkTypeCreateDTO);

    /**
     * 修改issueLinkType
     *
     * @param issueLinkTypeDTO issueLinkTypeDTO
     * @return IssueLinkTypeDTO
     */
    IssueLinkTypeDTO updateIssueLinkType(IssueLinkTypeDTO issueLinkTypeDTO);

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
     * @return IssueLinkTypeDTO
     */
    IssueLinkTypeDTO queryIssueLinkType(Long projectId, Long linkTypeId);

    /**
     * 创建项目初始化issueLinkType
     *
     * @param projectId projectId
     */
    void initIssueLinkType(Long projectId);

    boolean queryIssueLinkTypeName(Long projectId, String issueLinkTypeName, Long issueLinkTypeId);
}
