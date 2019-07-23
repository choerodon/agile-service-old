package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.FieldValueDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/8
 */
public interface FieldValueMapper extends Mapper<FieldValueDTO> {

    List<FieldValueDTO> queryList(@Param("projectId") Long projectId, @Param("instanceId") Long instanceId, @Param("schemeCode") String schemeCode, @Param("fieldId") Long fieldId);

    void batchInsert(@Param("projectId") Long projectId, @Param("instanceId") Long instanceId, @Param("schemeCode") String schemeCode, @Param("fieldValues") List<FieldValueDTO> fieldValues);

    void deleteByOptionIds(@Param("fieldId") Long fieldId, @Param("optionIds") List<Long> optionIds);

    void deleteList(@Param("projectId") Long projectId, @Param("instanceId") Long instanceId, @Param("schemeCode") String schemeCode, @Param("fieldId") Long fieldId);

    List<Long> sortIssueIdsByFieldValue(@Param("organizationId") Long organizationId, @Param("projectId") Long projectId, @Param("fieldId") Long fieldId, @Param("sortSql") String sortSql);
}
