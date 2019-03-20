package io.choerodon.agile.domain.agile.rule;

import io.choerodon.agile.domain.agile.entity.IssueLinkE;
import io.choerodon.agile.infra.dataobject.IssueLinkDO;
import io.choerodon.agile.infra.mapper.IssueLinkMapper;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Autowired
    private IssueLinkMapper issueLinkMapper;

    public void verifyCreateData(IssueLinkE issueLinkE) {
        if (issueLinkE.getLinkTypeId() == null) {
            throw new CommonException("error.issueLink.LinkTypeId");
        }
        if (issueLinkE.getLinkedIssueId() == null) {
            throw new CommonException("error.issueLink.LinkIssueId");
        }
        if (issueLinkE.getProjectId() == null) {
            throw new CommonException("error.issueLink.ProjectId");
        }
        if (issueMapper.selectByPrimaryKey(issueLinkE.getIssueId()) == null) {
            throw new CommonException("error.issueLink.IssueNotFound");
        }
        if (issueLinkTypeMapper.selectByPrimaryKey(issueLinkE.getLinkTypeId()) == null) {
            throw new CommonException("error.issueLink.LinkTypeId");
        }
    }

    public Boolean checkUniqueLink(IssueLinkE issueLinkE) {
        IssueLinkDO issueLinkDO = new IssueLinkDO();
        issueLinkDO.setIssueId(issueLinkE.getIssueId());
        issueLinkDO.setLinkTypeId(issueLinkE.getLinkTypeId());
        issueLinkDO.setLinkedIssueId(issueLinkE.getLinkedIssueId());
        issueLinkDO.setProjectId(issueLinkE.getProjectId());
        List<IssueLinkDO> issueLinkDOList = issueLinkMapper.select(issueLinkDO);
        if (issueLinkDOList != null && !issueLinkDOList.isEmpty()) {
            return false;
        }
        return true;
    }
}
