package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.*;
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

    public List<IssueSearchDTO> doListToDTO(List<IssueSearchDO> issueSearchDOList, Map<Long, UserMessageDO> usersMap, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        if (issueSearchDOList != null && !issueSearchDOList.isEmpty()) {
            List<IssueSearchDTO> issueSearchDTOList = new ArrayList<>(issueSearchDOList.size());
            issueSearchDOList.forEach(issueSearch -> {
                String assigneeName = usersMap.get(issueSearch.getAssigneeId()) != null ? usersMap.get(issueSearch.getAssigneeId()).getName() : null;
                String imageUrl = assigneeName != null ? usersMap.get(issueSearch.getAssigneeId()).getImageUrl() : null;
                issueSearch.setAssigneeName(assigneeName);
                issueSearch.setImageUrl(imageUrl);
                issueSearch.setPriorityDTO(priorityMap.get(issueSearch.getPriorityId()));
                issueSearch.setStatusMapDTO(statusMapDTOMap.get(issueSearch.getStatusId()));
                issueSearch.setIssueTypeDTO(issueTypeDTOMap.get(issueSearch.getIssueTypeId()));
                issueSearchDTOList.add(toTarget(issueSearch, IssueSearchDTO.class));
            });
            return issueSearchDTOList;
        } else {
            return null;
        }
    }

    public List<AssigneeIssueDTO> doListToAssigneeIssueDTO(List<AssigneeIssueDO> assigneeIssueDOList, Map<Long, UserMessageDO> usersMap) {
        if (assigneeIssueDOList != null && !assigneeIssueDOList.isEmpty()) {
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
        } else {
            return null;
        }

    }

}
