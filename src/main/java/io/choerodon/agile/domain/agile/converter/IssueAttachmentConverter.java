package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.infra.dataobject.IssueAttachmentDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.vo.IssueAttachmentVO;
import io.choerodon.agile.domain.agile.entity.IssueAttachmentE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 20:08:38
 */
@Component
public class IssueAttachmentConverter implements ConvertorI<IssueAttachmentE, IssueAttachmentDTO, IssueAttachmentVO> {

    @Override
    public IssueAttachmentE dtoToEntity(IssueAttachmentVO issueAttachmentVO) {
        IssueAttachmentE issueAttachmentE = new IssueAttachmentE();
        BeanUtils.copyProperties(issueAttachmentVO, issueAttachmentE);
        return issueAttachmentE;
    }

    @Override
    public IssueAttachmentE doToEntity(IssueAttachmentDTO issueAttachmentDTO) {
        IssueAttachmentE issueAttachmentE = new IssueAttachmentE();
        BeanUtils.copyProperties(issueAttachmentDTO, issueAttachmentE);
        return issueAttachmentE;
    }

    @Override
    public IssueAttachmentVO entityToDto(IssueAttachmentE issueAttachmentE) {
        IssueAttachmentVO issueAttachmentVO = new IssueAttachmentVO();
        BeanUtils.copyProperties(issueAttachmentE, issueAttachmentVO);
        return issueAttachmentVO;
    }

    @Override
    public IssueAttachmentDTO entityToDo(IssueAttachmentE issueAttachmentE) {
        IssueAttachmentDTO issueAttachmentDTO = new IssueAttachmentDTO();
        BeanUtils.copyProperties(issueAttachmentE, issueAttachmentDTO);
        return issueAttachmentDTO;
    }

    @Override
    public IssueAttachmentVO doToDto(IssueAttachmentDTO issueAttachmentDTO) {
        IssueAttachmentVO issueAttachmentVO = new IssueAttachmentVO();
        BeanUtils.copyProperties(issueAttachmentDTO, issueAttachmentVO);
        return issueAttachmentVO;
    }

    @Override
    public IssueAttachmentDTO dtoToDo(IssueAttachmentVO issueAttachmentVO) {
        IssueAttachmentDTO issueAttachmentDTO = new IssueAttachmentDTO();
        BeanUtils.copyProperties(issueAttachmentVO, issueAttachmentDTO);
        return issueAttachmentDTO;
    }
}