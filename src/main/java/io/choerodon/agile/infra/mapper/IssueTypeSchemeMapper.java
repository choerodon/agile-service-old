package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.IssueTypeSchemeSearchVO;
import io.choerodon.agile.infra.dataobject.IssueTypeSchemeDTO;
import io.choerodon.agile.infra.dataobject.IssueTypeSchemeWithInfoDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/10
 */
@Component
public interface IssueTypeSchemeMapper extends Mapper<IssueTypeSchemeDTO> {
    List<Long> selectIssueTypeSchemeIds(@Param("organizationId") Long organizationId, @Param("issueTypeSchemeSearchVO") IssueTypeSchemeSearchVO issueTypeSchemeSearchVO);

    List<IssueTypeSchemeWithInfoDTO> queryIssueTypeSchemeList(@Param("organizationId") Long organizationId, @Param("issueTypeSchemeIds") List<Long> issueTypeSchemeIds);
}
