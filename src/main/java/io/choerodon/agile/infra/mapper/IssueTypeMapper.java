package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.IssueTypeSearchVO;
import io.choerodon.agile.infra.dataobject.IssueTypeDTO;
import io.choerodon.agile.infra.dataobject.IssueTypeWithInfoDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/8
 */
@Component
public interface IssueTypeMapper extends Mapper<IssueTypeDTO> {

    List<IssueTypeDTO> queryBySchemeId(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);

    List<IssueTypeDTO> queryByOrgId(@Param("organizationId") Long organizationId);

    List<Long> selectIssueTypeIds(@Param("organizationId") Long organizationId, @Param("issueTypeSearchVO") IssueTypeSearchVO issueTypeSearchVO);

    List<IssueTypeWithInfoDTO> queryIssueTypeList(@Param("organizationId") Long organizationId, @Param("issueTypeIds") List<Long> issueTypeIds);
}
