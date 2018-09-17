package io.choerodon.agile.domain.agile.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO;
import io.choerodon.agile.api.dto.IssueLinkTypeDTO;
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDO;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.core.exception.CommonException;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/22
 */
@Component
public class IssueLinkTypeRule {

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;

    public void verifyCreateData(IssueLinkTypeCreateDTO issueLinkTypeCreateDTO, Long projectId) {
        if (issueLinkTypeCreateDTO.getInWard() == null) {
            throw new CommonException("error.IssueLinkType.inWard");
        }
        if (issueLinkTypeCreateDTO.getOutWard() == null) {
            throw new CommonException("error.IssueLinkType.outWard");
        }
        if (issueLinkTypeCreateDTO.getLinkName() == null) {
            throw new CommonException("error.IssueLinkType.linkName");
        }
        issueLinkTypeCreateDTO.setProjectId(projectId);
    }

    public void verifyDeleteData(Long issueLinkTypeId, Long toIssueLinkTypeId, Long projectId) {
        IssueLinkTypeDO issueLinkTypeDO = new IssueLinkTypeDO();
        issueLinkTypeDO.setLinkTypeId(issueLinkTypeId);
        issueLinkTypeDO.setProjectId(projectId);
        if (issueLinkTypeMapper.selectOne(issueLinkTypeDO) == null) {
            throw new CommonException("error.IssueLinkType.notFound");
        }
        issueLinkTypeDO.setLinkTypeId(toIssueLinkTypeId);
        if (toIssueLinkTypeId != null && issueLinkTypeMapper.selectOne(issueLinkTypeDO) == null) {
            throw new CommonException("error.IssueLinkType.notFound");
        }
    }

    public void verifyUpdateData(IssueLinkTypeDTO issueLinkTypeDTO, Long projectId) {
        if (issueLinkTypeDTO.getLinkTypeId() == null) {
            throw new CommonException("error.IssueLinkType.linkTypeId");
        }
        issueLinkTypeDTO.setProjectId(projectId);
    }

    public void verifyLinkName(Long projectId, String issueLinkTypeName, Long issueLinkTypeId) {
        if (issueLinkTypeMapper.queryIssueLinkName(projectId, issueLinkTypeName, issueLinkTypeId) > 0) {
            throw new CommonException("error.IssueLinkTypeName.isExisted");
        }
    }
}
