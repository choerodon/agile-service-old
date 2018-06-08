package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IssueCommentCreateDTO;
import io.choerodon.agile.api.dto.IssueCommentUpdateDTO;
import io.choerodon.agile.domain.agile.entity.IssueCommentE;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 */
@Component
public class IssueCommentAssembler {

    /**
     * issueCommentCreateDTO转换到issueCommentE
     *
     * @param issueCommentCreateDTO issueCommentCreateDTO
     * @return IssueCommentE
     */
    public IssueCommentE issueCommentCreateDtoToEntity(IssueCommentCreateDTO issueCommentCreateDTO) {
        IssueCommentE issueCommentE = new IssueCommentE();
        BeanUtils.copyProperties(issueCommentCreateDTO, issueCommentE);
        return issueCommentE;
    }

    /**
     * IssueCommentUpdateDTO转换到IssueCommentE
     *
     * @param issueCommentUpdateDTO issueCommentUpdateDTO
     * @return IssueCommentE
     */
    public IssueCommentE issueCommentUpdateDtoToEntity(IssueCommentUpdateDTO issueCommentUpdateDTO) {
        IssueCommentE issueCommentE = new IssueCommentE();
        BeanUtils.copyProperties(issueCommentUpdateDTO, issueCommentE);
        return issueCommentE;
    }
}
