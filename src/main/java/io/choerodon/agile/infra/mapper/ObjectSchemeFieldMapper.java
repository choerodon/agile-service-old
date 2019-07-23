package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.ObjectSchemeFieldSearchVO;
import io.choerodon.agile.infra.dataobject.ObjectSchemeFieldDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public interface ObjectSchemeFieldMapper extends Mapper<ObjectSchemeFieldDTO> {
    /**
     * 根据对象方案编码查询方案字段
     *
     * @param organizationId
     * @return
     */
    List<ObjectSchemeFieldDTO> listQuery(@Param("organizationId") Long organizationId, @Param("projectId") Long projectId, @Param("searchVO") ObjectSchemeFieldSearchVO searchVO);

    ObjectSchemeFieldDTO queryById(@Param("fieldId") Long fieldId);

    ObjectSchemeFieldDTO queryByFieldCode(@Param("organizationId") Long organizationId, @Param("projectId") Long projectId, @Param("fieldCode") String fieldCode);
}
