package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueBranchDTO;
import io.choerodon.agile.domain.agile.entity.IssueBranchE;
import io.choerodon.agile.infra.dataobject.IssueBranchDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueBranchConverter implements ConvertorI<IssueBranchE, IssueBranchDO, IssueBranchDTO> {

    @Override
    public IssueBranchE dtoToEntity(IssueBranchDTO issueBranchDTO) {
        IssueBranchE issueBranchE = new IssueBranchE();
        BeanUtils.copyProperties(issueBranchDTO, issueBranchE);
        return issueBranchE;
    }

    @Override
    public IssueBranchDTO entityToDto(IssueBranchE issueBranchE) {
        IssueBranchDTO issueStatusDTO = new IssueBranchDTO();
        BeanUtils.copyProperties(issueBranchE, issueStatusDTO);
        return issueStatusDTO;
    }

    @Override
    public IssueBranchE doToEntity(IssueBranchDO issueBranchDO) {
        IssueBranchE issueBranchE = new IssueBranchE();
        BeanUtils.copyProperties(issueBranchDO, issueBranchE);
        return issueBranchE;
    }

    @Override
    public IssueBranchDO entityToDo(IssueBranchE issueBranchE) {
        IssueBranchDO issueBranchDO = new IssueBranchDO();
        BeanUtils.copyProperties(issueBranchE, issueBranchDO);
        return issueBranchDO;
    }

    @Override
    public IssueBranchDTO doToDto(IssueBranchDO issueBranchDO) {
        IssueBranchDTO issueBranchDTO = new IssueBranchDTO();
        BeanUtils.copyProperties(issueBranchDO, issueBranchDTO);
        return issueBranchDTO;
    }

    @Override
    public IssueBranchDO dtoToDo(IssueBranchDTO issueBranchDTO) {
        IssueBranchDO issueBranchDO = new IssueBranchDO();
        BeanUtils.copyProperties(issueBranchDTO, issueBranchDO);
        return issueBranchDO;
    }
}
