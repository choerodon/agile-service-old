package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.FieldOptionDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface FieldOptionMapper extends Mapper<FieldOptionDTO> {

    /**
     * 根据字段id获取字段选项
     *
     * @param organizationId
     * @param fieldId
     * @return
     */
    List<FieldOptionDTO> selectByFieldId(@Param("organizationId") Long organizationId, @Param("fieldId") Long fieldId);

    /**
     * 根据字段id列表获取字段选项
     *
     * @param organizationId
     * @param fieldIds
     * @return
     */
    List<FieldOptionDTO> selectByFieldIds(@Param("organizationId") Long organizationId, @Param("fieldIds") List<Long> fieldIds);

    /**
     * 根据optionIds查询对象
     *
     * @param organizationId
     * @param optionIds
     * @return
     */
    List<FieldOptionDTO> selectByOptionIds(@Param("organizationId") Long organizationId, @Param("optionIds") List<Long> optionIds);
}
