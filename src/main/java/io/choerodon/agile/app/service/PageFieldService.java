package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.ObjectSchemeFieldDTO;
import io.choerodon.agile.infra.dataobject.PageFieldDTO;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface PageFieldService {

    PageFieldDTO baseCreate(PageFieldDTO pageField);

    void baseDelete(Long pageFieldId);

    void baseUpdate(PageFieldDTO pageField);

    PageFieldDTO baseQueryById(Long organizationId, Long projectId, Long pageFieldId);

    /**
     * 根据pageCode和context获取pageField，不存在则创建
     *
     * @param organizationId
     * @param projectId
     * @param pageCode
     * @param context
     * @return
     */
    List<PageFieldDTO> queryPageField(Long organizationId, Long projectId, String pageCode, String context);

    /**
     * 组织层/项目层 根据页面编码获取字段列表
     *
     * @param organizationId
     * @param projectId
     * @param pageCode
     * @param context
     * @return
     */
    Map<String, Object> listQuery(Long organizationId, Long projectId, String pageCode, String context);

    /**
     * 组织层/项目层 调整字段顺序
     *
     * @param organizationId
     * @param projectId
     * @param adjustOrder
     */
    PageFieldVO adjustFieldOrder(Long organizationId, Long projectId, String pageCode, AdjustOrderVO adjustOrder);

    /**
     * 组织层/项目层 更新页面字段
     *
     * @param organizationId
     * @param projectId
     * @param fieldId
     * @param updateDTO
     * @return
     */
    PageFieldVO update(Long organizationId, Long projectId, String pageCode, Long fieldId, PageFieldUpdateVO updateDTO);

    /**
     * 组织层初始化页面字段
     *
     * @param organizationId
     */
    void initPageFieldByOrg(Long organizationId);

    /**
     * 组织层 创建页面字段
     *
     * @param organizationId
     * @param field
     */
    void createByFieldWithOrg(Long organizationId, ObjectSchemeFieldDTO field);

    /**
     * 项目层 创建页面字段
     *
     * @param organizationId
     * @param projectId
     * @param field
     */
    void createByFieldWithPro(Long organizationId, Long projectId, ObjectSchemeFieldDTO field);

    /**
     * 删除字段
     *
     * @param fieldId
     */
    void deleteByFieldId(Long fieldId);

    /**
     * 界面上获取字段列表，带有字段选项
     *
     * @param organizationId
     * @param projectId
     * @param paramDTO
     * @return
     */
    List<PageFieldViewVO> queryPageFieldViewList(Long organizationId, Long projectId, PageFieldViewParamVO paramDTO);

    /**
     * 根据实例id从界面上获取字段列表，带有字段值、字段选项
     *
     * @param organizationId
     * @param projectId
     * @param instanceId
     * @param paramDTO
     * @return
     */
    List<PageFieldViewVO> queryPageFieldViewListWithInstanceId(Long organizationId, Long projectId, Long instanceId, PageFieldViewParamVO paramDTO);

    /**
     * 根据实例ids获取全部自定义字段的CodeValue键值对
     *
     * @param organizationId
     * @param projectId
     * @param instanceIds
     * @return
     */
    Map<Long, Map<String, String>> queryFieldValueWithIssueIdsForAgileExport(Long organizationId, Long projectId, List<Long> instanceIds);
}
