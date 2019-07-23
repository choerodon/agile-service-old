package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.IssueTypeSchemeConfigDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/8/10
 */
@Component
public interface IssueTypeSchemeConfigMapper extends Mapper<IssueTypeSchemeConfigDTO> {
    void deleteBySchemeId(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);
}
