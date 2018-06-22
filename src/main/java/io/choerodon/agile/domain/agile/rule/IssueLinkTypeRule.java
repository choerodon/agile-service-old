package io.choerodon.agile.domain.agile.rule;

import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/22
 */
@Component
public class IssueLinkTypeRule {

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;

    public void verifyCreateData(IssueLinkTypeCreateDTO issueLinkTypeCreateDTO) {
        if(issueLinkTypeCreateDTO.getInWard()==null){
            throw new CommonException("error.IssueLinkType.inWard");
        }
        if(issueLinkTypeCreateDTO.getOutWard()==null){
            throw new CommonException("error.IssueLinkType.outWard");
        }
        if(issueLinkTypeCreateDTO.getLinkName()==null){
            throw new CommonException("error.IssueLinkType.linkName");
        }
    }

    public void verifyDeleteData(Long issueLinkTypeId, Long toIssueLinkTypeId) {
        if(issueLinkTypeMapper.selectByPrimaryKey(issueLinkTypeId)==null){
            throw new CommonException("error.IssueLinkType.notFound");
        }
        if(toIssueLinkTypeId!=null&&issueLinkTypeMapper.selectByPrimaryKey(toIssueLinkTypeId)==null){
            throw new CommonException("error.IssueLinkType.notFound");
        }
    }
}
