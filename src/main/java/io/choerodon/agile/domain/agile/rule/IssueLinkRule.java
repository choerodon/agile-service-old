package io.choerodon.agile.domain.agile.rule;

import io.choerodon.agile.domain.agile.entity.IssueLinkE;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Component
public class IssueLinkRule {

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;
    @Autowired
    private IssueMapper issueMapper;

    public void verifyCreateData(IssueLinkE issueLinkE) {
        if (issueLinkE.getLinkTypeId() == null) {
            throw new CommonException("error.issueLink.LinkTypeId");
        }
        if (issueLinkE.getLinkedIssueId() == null) {
            throw new CommonException("error.issueLink.LinkIssueId");
        }
        if (issueMapper.selectByPrimaryKey(issueLinkE.getIssueId()) == null) {
            throw new CommonException("error.issueLink.IssueNotFound");
        }
        if (issueLinkTypeMapper.selectByPrimaryKey(issueLinkE.getLinkTypeId()) == null) {
            throw new CommonException("error.issueLink.LinkTypeId");
        }
    }
}
