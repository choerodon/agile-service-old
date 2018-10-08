package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.AssigneeIssueDTO;
import io.choerodon.agile.api.dto.IssueSearchDTO;
import io.choerodon.agile.infra.common.utils.ColorUtil;
import io.choerodon.agile.infra.dataobject.AssigneeIssueDO;
import io.choerodon.agile.infra.dataobject.IssueSearchDO;
import io.choerodon.agile.infra.dataobject.LookupValueDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.mapper.LookupValueMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jian_zhang02@163.com on 2018/5/16.
 */
@Component
public class IssueSearchAssembler extends AbstractAssembler {

    @Autowired
    private LookupValueMapper lookupValueMapper;

    private static final String ISSUE_STATUS_COLOR = "issue_status_color";

    public List<IssueSearchDTO> doListToDTO(List<IssueSearchDO> issueSearchDOList, Map<Long, UserMessageDO> usersMap) {
        List<IssueSearchDTO> issueSearchDTOList = new ArrayList<>(issueSearchDOList.size());
        LookupValueDO lookupValueDO = new LookupValueDO();
        lookupValueDO.setTypeCode(ISSUE_STATUS_COLOR);
        Map<String, String> lookupValueMap = lookupValueMapper.select(lookupValueDO).stream().collect(Collectors.toMap(LookupValueDO::getValueCode, LookupValueDO::getName));
        issueSearchDOList.forEach(issueSearch -> {
            String assigneeName = usersMap.get(issueSearch.getAssigneeId()) != null ? usersMap.get(issueSearch.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(issueSearch.getAssigneeId()).getImageUrl() : null;
            issueSearch.setAssigneeName(assigneeName);
            issueSearch.setImageUrl(imageUrl);
            issueSearch.setStatusColor(ColorUtil.initializationStatusColor(issueSearch.getCategoryCode(), lookupValueMap));
            issueSearchDTOList.add(toTarget(issueSearch, IssueSearchDTO.class));
        });
        return issueSearchDTOList;
    }

    public List<AssigneeIssueDTO> doListToAssigneeIssueDTO(List<AssigneeIssueDO> assigneeIssueDOList, Map<Long, UserMessageDO> usersMap) {
        List<AssigneeIssueDTO> assigneeIssues = new ArrayList<>(assigneeIssueDOList.size());
        assigneeIssueDOList.forEach(assigneeIssueDO -> {
            String assigneeName = usersMap.get(assigneeIssueDO.getAssigneeId()) != null ? usersMap.get(assigneeIssueDO.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(assigneeIssueDO.getAssigneeId()).getImageUrl() : null;
            AssigneeIssueDTO assigneeIssueDTO = new AssigneeIssueDTO();
            BeanUtils.copyProperties(assigneeIssueDO, assigneeIssueDTO);
            assigneeIssueDTO.setAssigneeName(assigneeName);
            assigneeIssueDTO.setImageUrl(imageUrl);
            assigneeIssues.add(assigneeIssueDTO);
        });
        return assigneeIssues;
    }

}
