package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.FieldOptionUpdateVO;
import io.choerodon.agile.api.vo.FieldOptionVO;
import io.choerodon.agile.api.vo.PageFieldViewVO;
import io.choerodon.agile.infra.dataobject.FieldOptionDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface FieldOptionService {

    FieldOptionDTO baseCreate(FieldOptionDTO option);

    void baseDelete(Long optionId);

    void baseUpdate(FieldOptionDTO option);

    FieldOptionDTO baseQueryById(Long organizationId, Long optionId);

    /**
     * 处理字段选项
     *
     * @param organizationId
     * @param fieldId
     * @param newOptions
     */
    String handleFieldOption(Long organizationId, Long fieldId, List<FieldOptionUpdateVO> newOptions);

    /**
     * 组织层/项目层 根据字段id获取字段选项列表
     *
     * @param organizationId
     * @param fieldId
     * @return
     */
    List<FieldOptionVO> queryByFieldId(Long organizationId, Long fieldId);

    /**
     * 组织层/项目层 创建字段选项
     *
     * @param organizationId
     * @param fieldId
     * @param updateDTO
     * @return
     */
    void create(Long organizationId, Long fieldId, FieldOptionUpdateVO updateDTO);

    /**
     * 组织层/项目层 根据字段id删除所有字段选项
     *
     * @param organizationId
     * @param fieldId
     */
    void deleteByFieldId(Long organizationId, Long fieldId);

    /**
     * 填充字段选项
     *
     * @param organizationId
     * @param projectId
     * @param pageFieldViews
     */
    void fillOptions(Long organizationId, Long projectId, List<PageFieldViewVO> pageFieldViews);
}
