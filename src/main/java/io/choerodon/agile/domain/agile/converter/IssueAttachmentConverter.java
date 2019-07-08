package io.choerodon.agile.domain.agile.converter;


import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.vo.IssueAttachmentDTO;
import io.choerodon.agile.infra.dataobject.IssueAttachmentDO;
import io.choerodon.agile.domain.agile.entity.IssueAttachmentE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 20:08:38
 */
@Component
public class IssueAttachmentConverter implements ConvertorI<IssueAttachmentE, IssueAttachmentDO, IssueAttachmentDTO> {

    @Override
    public IssueAttachmentE dtoToEntity(IssueAttachmentDTO issueAttachmentDTO) {
        IssueAttachmentE issueAttachmentE = new IssueAttachmentE();
        BeanUtils.copyProperties(issueAttachmentDTO, issueAttachmentE);
        return issueAttachmentE;
    }

    @Override
    public IssueAttachmentE doToEntity(IssueAttachmentDO issueAttachmentDO) {
        IssueAttachmentE issueAttachmentE = new IssueAttachmentE();
        BeanUtils.copyProperties(issueAttachmentDO, issueAttachmentE);
        return issueAttachmentE;
    }

    @Override
    public IssueAttachmentDTO entityToDto(IssueAttachmentE issueAttachmentE) {
        IssueAttachmentDTO issueAttachmentDTO = new IssueAttachmentDTO();
        BeanUtils.copyProperties(issueAttachmentE, issueAttachmentDTO);
        return issueAttachmentDTO;
    }

    @Override
    public IssueAttachmentDO entityToDo(IssueAttachmentE issueAttachmentE) {
        IssueAttachmentDO issueAttachmentDO = new IssueAttachmentDO();
        BeanUtils.copyProperties(issueAttachmentE, issueAttachmentDO);
        return issueAttachmentDO;
    }

    @Override
    public IssueAttachmentDTO doToDto(IssueAttachmentDO issueAttachmentDO) {
        IssueAttachmentDTO issueAttachmentDTO = new IssueAttachmentDTO();
        BeanUtils.copyProperties(issueAttachmentDO, issueAttachmentDTO);
        return issueAttachmentDTO;
    }

    @Override
    public IssueAttachmentDO dtoToDo(IssueAttachmentDTO issueAttachmentDTO) {
        IssueAttachmentDO issueAttachmentDO = new IssueAttachmentDO();
        BeanUtils.copyProperties(issueAttachmentDTO, issueAttachmentDO);
        return issueAttachmentDO;
    }
}