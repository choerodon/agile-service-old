package io.choerodon.agile.api.validator;

import io.choerodon.agile.infra.dataobject.IssueLinkDTO;
import io.choerodon.agile.infra.mapper.IssueLinkMapper;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueLinkValidator {

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private IssueLinkMapper issueLinkMapper;

    public void verifyCreateData(IssueLinkDTO issueLinkDTO) {
        if (issueLinkDTO.getLinkTypeId() == null) {
            throw new CommonException("error.issueLink.LinkTypeId");
        }
        if (issueLinkDTO.getLinkedIssueId() == null) {
            throw new CommonException("error.issueLink.LinkIssueId");
        }
        if (issueLinkDTO.getProjectId() == null) {
            throw new CommonException("error.issueLink.ProjectId");
        }
        if (issueMapper.selectByPrimaryKey(issueLinkDTO.getIssueId()) == null) {
            throw new CommonException("error.issueLink.IssueNotFound");
        }
        if (issueLinkTypeMapper.selectByPrimaryKey(issueLinkDTO.getLinkTypeId()) == null) {
            throw new CommonException("error.issueLink.LinkTypeId");
        }
    }

    public Boolean checkUniqueLink(IssueLinkDTO issueLinkDTO) {
        IssueLinkDTO issueLink = new IssueLinkDTO();
        issueLink.setIssueId(issueLinkDTO.getIssueId());
        issueLink.setLinkTypeId(issueLinkDTO.getLinkTypeId());
        issueLink.setLinkedIssueId(issueLinkDTO.getLinkedIssueId());
        issueLink.setProjectId(issueLinkDTO.getProjectId());
        List<IssueLinkDTO> issueLinkDTOList = issueLinkMapper.select(issueLink);
        if (issueLinkDTOList != null && !issueLinkDTOList.isEmpty()) {
            return false;
        }
        return true;
    }

}
