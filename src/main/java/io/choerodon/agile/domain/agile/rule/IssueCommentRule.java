package io.choerodon.agile.domain.agile.rule;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.IssueCommentCreateDTO;
import io.choerodon.agile.infra.dataobject.IssueCommentDO;
import io.choerodon.agile.infra.mapper.IssueCommentMapper;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 */
@Component
public class IssueCommentRule {

    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueCommentMapper issueCommentMapper;

    private static final String COMMENT_ID = "commentId";

    public void verifyCreateData(IssueCommentCreateDTO issueCommentCreateDTO) {
        if (issueCommentCreateDTO.getIssueId() == null) {
            throw new CommonException("error.IssueCommentRule.issueId");
        } else {
            if (issueMapper.selectByPrimaryKey(issueCommentCreateDTO.getIssueId()) == null) {
                throw new CommonException("error.IssueCommentRule.issue");
            }
        }
    }

    public void verifyUpdateData(Long projectId, JSONObject issueCommentUpdate) {
        if (issueCommentUpdate.get(COMMENT_ID) == null) {
            throw new CommonException("error.IssueCommentRule.commentId");
        }
        IssueCommentDO issueCommentDO = new IssueCommentDO();
        issueCommentDO.setCommentId(Long.parseLong(issueCommentUpdate.get(COMMENT_ID).toString()));
        issueCommentDO.setProjectId(projectId);
        if (issueCommentMapper.selectByPrimaryKey(issueCommentDO) == null) {
            throw new CommonException("error.IssueCommentRule.issueComment");
        }
    }
}
