package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.IssueLinkTypeCreateVO;
import io.choerodon.agile.api.vo.IssueLinkTypeVO;
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDTO;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueLinkTypeValidator {

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;

    public void verifyCreateData(IssueLinkTypeCreateVO issueLinkTypeCreateVO, Long projectId) {
        if (issueLinkTypeCreateVO.getInWard() == null) {
            throw new CommonException("error.IssueLinkType.inWard");
        }
        if (issueLinkTypeCreateVO.getOutWard() == null) {
            throw new CommonException("error.IssueLinkType.outWard");
        }
        if (issueLinkTypeCreateVO.getLinkName() == null) {
            throw new CommonException("error.IssueLinkType.linkName");
        }
        issueLinkTypeCreateVO.setProjectId(projectId);
    }

    public void verifyDeleteData(Long issueLinkTypeId, Long toIssueLinkTypeId, Long projectId) {
        IssueLinkTypeDTO issueLinkTypeDTO = new IssueLinkTypeDTO();
        issueLinkTypeDTO.setLinkTypeId(issueLinkTypeId);
        issueLinkTypeDTO.setProjectId(projectId);
        if (issueLinkTypeMapper.selectOne(issueLinkTypeDTO) == null) {
            throw new CommonException("error.IssueLinkType.notFound");
        }
        issueLinkTypeDTO.setLinkTypeId(toIssueLinkTypeId);
        if (toIssueLinkTypeId != null && issueLinkTypeMapper.selectOne(issueLinkTypeDTO) == null) {
            throw new CommonException("error.IssueLinkType.notFound");
        }
    }

    public void verifyUpdateData(IssueLinkTypeVO issueLinkTypeVO, Long projectId) {
        if (issueLinkTypeVO.getLinkTypeId() == null) {
            throw new CommonException("error.IssueLinkType.linkTypeId");
        }
        issueLinkTypeVO.setProjectId(projectId);
    }

    public void verifyIssueLinkTypeName(Long projectId, String issueLinkTypeName, Long issueLinkTypeId) {
        if (issueLinkTypeMapper.queryIssueLinkTypeName(projectId, issueLinkTypeName, issueLinkTypeId) > 0) {
            throw new CommonException("error.IssueLinkTypeName.isExisted");
        }
    }
}
