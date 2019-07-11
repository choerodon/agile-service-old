package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.VersionIssueRelVO;
import io.choerodon.agile.infra.dataobject.VersionIssueRelDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
@Component
public class VersionIssueRelConverter implements ConvertorI<VersionIssueRelE, VersionIssueRelDTO, VersionIssueRelVO> {

    @Override
    public VersionIssueRelE dtoToEntity(VersionIssueRelVO versionIssueRelVO) {
        VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
        BeanUtils.copyProperties(versionIssueRelVO, versionIssueRelE);
        return versionIssueRelE;
    }

    @Override
    public VersionIssueRelE doToEntity(VersionIssueRelDTO versionIssueRelDTO) {
        VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
        BeanUtils.copyProperties(versionIssueRelDTO, versionIssueRelE);
        return versionIssueRelE;
    }

    @Override
    public VersionIssueRelVO entityToDto(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelVO versionIssueRelVO = new VersionIssueRelVO();
        BeanUtils.copyProperties(versionIssueRelE, versionIssueRelVO);
        return versionIssueRelVO;
    }

    @Override
    public VersionIssueRelDTO entityToDo(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        BeanUtils.copyProperties(versionIssueRelE, versionIssueRelDTO);
        return versionIssueRelDTO;
    }

    @Override
    public VersionIssueRelVO doToDto(VersionIssueRelDTO versionIssueRelDTO) {
        VersionIssueRelVO versionIssueRelVO = new VersionIssueRelVO();
        BeanUtils.copyProperties(versionIssueRelDTO, versionIssueRelVO);
        return versionIssueRelVO;
    }

    @Override
    public VersionIssueRelDTO dtoToDo(VersionIssueRelVO versionIssueRelVO) {
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        BeanUtils.copyProperties(versionIssueRelVO, versionIssueRelDTO);
        return versionIssueRelDTO;
    }
}