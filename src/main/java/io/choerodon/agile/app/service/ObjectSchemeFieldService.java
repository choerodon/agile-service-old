package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.ObjectSchemeFieldDTO;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public interface ObjectSchemeFieldService {

    ObjectSchemeFieldDTO baseCreate(ObjectSchemeFieldDTO field);

    void baseDelete(Long fieldId);

    void baseUpdate(ObjectSchemeFieldDTO field);

    ObjectSchemeFieldDTO baseQueryById(Long organizationId, Long projectId, Long fieldId);

    List<ObjectSchemeFieldDTO> listQuery(Long organizationId, Long projectId, ObjectSchemeFieldSearchVO searchDTO);

    ObjectSchemeFieldDTO queryByFieldCode(Long organizationId, Long projectId, String fieldCode);
    /**
     * 组织层/项目层 获取字段列表
     *
     * @param organizationId
     * @param schemeCode
     * @return
     */
    Map<String, Object> listQuery(Long organizationId, Long projectId, String schemeCode);

    /**
     * 组织层/项目层 创建字段
     *
     * @param organizationId
     * @param projectId
     * @param fieldCreateDTO
     * @return
     */
    ObjectSchemeFieldDetailVO create(Long organizationId, Long projectId, ObjectSchemeFieldCreateVO fieldCreateDTO);

    /**
     * 组织层/项目层 查询字段详情
     *
     * @param organizationId
     * @param projectId
     * @param fieldId
     * @return
     */
    ObjectSchemeFieldDetailVO queryById(Long organizationId, Long projectId, Long fieldId);

    /**
     * 组织层/项目层 删除字段
     *
     * @param organizationId
     * @param projectId
     * @param fieldId
     */
    void delete(Long organizationId, Long projectId, Long fieldId);

    /**
     * 组织层/项目层 更新字段
     *
     * @param organizationId
     * @param projectId
     * @param fieldId
     * @param updateDTO
     * @return
     */
    ObjectSchemeFieldDetailVO update(Long organizationId, Long projectId, Long fieldId, ObjectSchemeFieldUpdateVO updateDTO);

    /**
     * 组织层/项目层 字段名称是否重复
     *
     * @param organizationId
     * @param projectId
     * @param name
     * @param schemeCode
     * @return
     */
    Boolean checkName(Long organizationId, Long projectId, String name, String schemeCode);

    /**
     * 组织层/项目层 字段编码是否重复
     *
     * @param organizationId
     * @param projectId
     * @param code
     * @param schemeCode
     * @return
     */
    Boolean checkCode(Long organizationId, Long projectId, String code, String schemeCode);

    List<AgileIssueHeadVO> getIssueHeadForAgile(Long organizationId, Long projectId, String schemeCode);
}
