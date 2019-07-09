package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.IssueAttachmentVO;
import io.choerodon.agile.api.vo.IssueCommentVO;
import io.choerodon.agile.infra.dataobject.IssueCommentDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.domain.agile.entity.IssueCommentE;

/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
@Component
public class IssueCommentConverter implements ConvertorI<IssueCommentE, IssueCommentDTO, IssueCommentVO> {

    @Override
    public IssueCommentE dtoToEntity(IssueCommentVO issueCommentVO) {
        IssueCommentE issueCommentE = new IssueCommentE();
        BeanUtils.copyProperties(issueCommentVO, issueCommentE);
        return issueCommentE;
    }

    @Override
    public IssueCommentE doToEntity(IssueCommentDTO issueCommentDTO) {
        IssueCommentE issueCommentE = new IssueCommentE();
        BeanUtils.copyProperties(issueCommentDTO, issueCommentE);
        return issueCommentE;
    }

    @Override
    public IssueCommentVO entityToDto(IssueCommentE issueCommentE) {
        IssueCommentVO issueCommentVO = new IssueCommentVO();
        BeanUtils.copyProperties(issueCommentE, issueCommentVO);
        return issueCommentVO;
    }

    @Override
    public IssueCommentDTO entityToDo(IssueCommentE issueCommentE) {
        IssueCommentDTO issueCommentDTO = new IssueCommentDTO();
        BeanUtils.copyProperties(issueCommentE, issueCommentDTO);
        return issueCommentDTO;
    }

    @Override
    public IssueCommentVO doToDto(IssueCommentDTO issueCommentDTO) {
        IssueCommentVO issueCommentVO = new IssueCommentVO();
        BeanUtils.copyProperties(issueCommentDTO, issueCommentVO);
        if (issueCommentDTO.getIssueAttachmentDTOList() != null && !issueCommentDTO.getIssueAttachmentDTOList().isEmpty()) {
            issueCommentVO.setIssueAttachmentVOList(ConvertHelper.convertList(issueCommentDTO.getIssueAttachmentDTOList(), IssueAttachmentVO.class));
        }
        return issueCommentVO;
    }

    @Override
    public IssueCommentDTO dtoToDo(IssueCommentVO issueCommentVO) {
        IssueCommentDTO issueCommentDTO = new IssueCommentDTO();
        BeanUtils.copyProperties(issueCommentVO, issueCommentDTO);
        return issueCommentDTO;
    }
}