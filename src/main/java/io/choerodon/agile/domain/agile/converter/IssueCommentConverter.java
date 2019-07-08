package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.IssueAttachmentDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.vo.IssueCommentDTO;
import io.choerodon.agile.infra.dataobject.IssueCommentDO;
import io.choerodon.agile.domain.agile.entity.IssueCommentE;

/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
@Component
public class IssueCommentConverter implements ConvertorI<IssueCommentE, IssueCommentDO, IssueCommentDTO> {

    @Override
    public IssueCommentE dtoToEntity(IssueCommentDTO issueCommentDTO) {
        IssueCommentE issueCommentE = new IssueCommentE();
        BeanUtils.copyProperties(issueCommentDTO, issueCommentE);
        return issueCommentE;
    }

    @Override
    public IssueCommentE doToEntity(IssueCommentDO issueCommentDO) {
        IssueCommentE issueCommentE = new IssueCommentE();
        BeanUtils.copyProperties(issueCommentDO, issueCommentE);
        return issueCommentE;
    }

    @Override
    public IssueCommentDTO entityToDto(IssueCommentE issueCommentE) {
        IssueCommentDTO issueCommentDTO = new IssueCommentDTO();
        BeanUtils.copyProperties(issueCommentE, issueCommentDTO);
        return issueCommentDTO;
    }

    @Override
    public IssueCommentDO entityToDo(IssueCommentE issueCommentE) {
        IssueCommentDO issueCommentDO = new IssueCommentDO();
        BeanUtils.copyProperties(issueCommentE, issueCommentDO);
        return issueCommentDO;
    }

    @Override
    public IssueCommentDTO doToDto(IssueCommentDO issueCommentDO) {
        IssueCommentDTO issueCommentDTO = new IssueCommentDTO();
        BeanUtils.copyProperties(issueCommentDO, issueCommentDTO);
        if (issueCommentDO.getIssueAttachmentDOList() != null && !issueCommentDO.getIssueAttachmentDOList().isEmpty()) {
            issueCommentDTO.setIssueAttachmentDTOList(ConvertHelper.convertList(issueCommentDO.getIssueAttachmentDOList(), IssueAttachmentDTO.class));
        }
        return issueCommentDTO;
    }

    @Override
    public IssueCommentDO dtoToDo(IssueCommentDTO issueCommentDTO) {
        IssueCommentDO issueCommentDO = new IssueCommentDO();
        BeanUtils.copyProperties(issueCommentDTO, issueCommentDO);
        return issueCommentDO;
    }
}