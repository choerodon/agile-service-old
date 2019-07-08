package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.AssigneeIssueDO;
import io.choerodon.agile.infra.dataobject.IssueSearchDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/5/16.
 */
@Component
public class IssueSearchAssembler extends AbstractAssembler {

    public List<IssueSearchDTO> doListToDTO(List<IssueSearchDO> issueSearchDOList, Map<Long, UserMessageDO> usersMap, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        if (issueSearchDOList != null && !issueSearchDOList.isEmpty()) {
            List<IssueSearchDTO> issueSearchDTOList = new ArrayList<>(issueSearchDOList.size());
            issueSearchDOList.forEach(issueSearch -> {
                UserMessageDO userMessageDO = usersMap.get(issueSearch.getAssigneeId());
                String assigneeName = userMessageDO != null ? userMessageDO.getName() : null;
                String imageUrl = assigneeName != null ? userMessageDO.getImageUrl() : null;
                String assigneeLoginName = userMessageDO != null ? userMessageDO.getLoginName() : null;
                String assigneeRealName = userMessageDO != null ? userMessageDO.getRealName() : null;
                issueSearch.setAssigneeName(assigneeName);
                issueSearch.setImageUrl(imageUrl);
                issueSearch.setAssigneeLoginName(assigneeLoginName);
                issueSearch.setAssigneeRealName(assigneeRealName);
                issueSearch.setPriorityDTO(priorityMap.get(issueSearch.getPriorityId()));
                issueSearch.setStatusMapDTO(statusMapDTOMap.get(issueSearch.getStatusId()));
                issueSearch.setIssueTypeDTO(issueTypeDTOMap.get(issueSearch.getIssueTypeId()));
                issueSearchDTOList.add(toTarget(issueSearch, IssueSearchDTO.class));
            });
            return issueSearchDTOList;
        } else {
            return new ArrayList<>();
        }
    }

    public List<AssigneeIssueDTO> doListToAssigneeIssueDTO(List<AssigneeIssueDO> assigneeIssueDOList, Map<Long, UserMessageDO> usersMap) {
        if (assigneeIssueDOList != null && !assigneeIssueDOList.isEmpty()) {
            List<AssigneeIssueDTO> assigneeIssues = new ArrayList<>(assigneeIssueDOList.size());
            assigneeIssueDOList.forEach(assigneeIssueDO -> {
                UserMessageDO userMessageDO = usersMap.get(assigneeIssueDO.getAssigneeId());
                String assigneeName = userMessageDO != null ? userMessageDO.getName() : null;
                String assigneeLoginName = userMessageDO != null ? userMessageDO.getLoginName() : null;
                String assigneeRealName = userMessageDO != null ? userMessageDO.getRealName() : null;
                String imageUrl = assigneeName != null ? userMessageDO.getImageUrl() : null;
                AssigneeIssueDTO assigneeIssueDTO = new AssigneeIssueDTO();
                BeanUtils.copyProperties(assigneeIssueDO, assigneeIssueDTO);
                assigneeIssueDTO.setAssigneeName(assigneeName);
                assigneeIssueDTO.setAssigneeLoginName(assigneeLoginName);
                assigneeIssueDTO.setAssigneeRealName(assigneeRealName);
                assigneeIssueDTO.setImageUrl(imageUrl);
                assigneeIssues.add(assigneeIssueDTO);
            });
            return assigneeIssues;
        } else {
            return new ArrayList<>();
        }

    }

}
