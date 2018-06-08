package io.choerodon.agile.domain.agile.converter;


import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.dto.VersionIssueRelDTO;
import io.choerodon.agile.infra.dataobject.VersionIssueRelDO;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
@Component
public class VersionIssueRelConverter implements ConvertorI<VersionIssueRelE, VersionIssueRelDO, VersionIssueRelDTO> {

    @Override
    public VersionIssueRelE dtoToEntity(VersionIssueRelDTO versionIssueRelDTO) {
        VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
        BeanUtils.copyProperties(versionIssueRelDTO, versionIssueRelE);
        return versionIssueRelE;
    }

    @Override
    public VersionIssueRelE doToEntity(VersionIssueRelDO versionIssueRelDO) {
        VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
        BeanUtils.copyProperties(versionIssueRelDO, versionIssueRelE);
        return versionIssueRelE;
    }

    @Override
    public VersionIssueRelDTO entityToDto(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        BeanUtils.copyProperties(versionIssueRelE, versionIssueRelDTO);
        return versionIssueRelDTO;
    }

    @Override
    public VersionIssueRelDO entityToDo(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
        BeanUtils.copyProperties(versionIssueRelE, versionIssueRelDO);
        return versionIssueRelDO;
    }

    @Override
    public VersionIssueRelDTO doToDto(VersionIssueRelDO versionIssueRelDO) {
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        BeanUtils.copyProperties(versionIssueRelDO, versionIssueRelDTO);
        return versionIssueRelDTO;
    }

    @Override
    public VersionIssueRelDO dtoToDo(VersionIssueRelDTO versionIssueRelDTO) {
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
        BeanUtils.copyProperties(versionIssueRelDTO, versionIssueRelDO);
        return versionIssueRelDO;
    }
}