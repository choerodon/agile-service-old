package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.AssigneeIssueDTO;
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

    public List<IssueSearchDTO> doListToDTO(List<IssueSearchDO> issueSearchDOList, Map<Long, UserMessageDO> usersMap, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
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
                issueSearch.setStatusMapVO(statusMapDTOMap.get(issueSearch.getStatusId()));
                issueSearch.setIssueTypeDTO(issueTypeDTOMap.get(issueSearch.getIssueTypeId()));
                issueSearchDTOList.add(toTarget(issueSearch, IssueSearchDTO.class));
            });
            return issueSearchDTOList;
        } else {
            return new ArrayList<>();
        }
    }

    public List<AssigneeIssueVO> doListToAssigneeIssueDTO(List<AssigneeIssueDTO> assigneeIssueDTOList, Map<Long, UserMessageDO> usersMap) {
        if (assigneeIssueDTOList != null && !assigneeIssueDTOList.isEmpty()) {
            List<AssigneeIssueVO> assigneeIssues = new ArrayList<>(assigneeIssueDTOList.size());
            assigneeIssueDTOList.forEach(assigneeIssueDO -> {
                UserMessageDO userMessageDO = usersMap.get(assigneeIssueDO.getAssigneeId());
                String assigneeName = userMessageDO != null ? userMessageDO.getName() : null;
                String assigneeLoginName = userMessageDO != null ? userMessageDO.getLoginName() : null;
                String assigneeRealName = userMessageDO != null ? userMessageDO.getRealName() : null;
                String imageUrl = assigneeName != null ? userMessageDO.getImageUrl() : null;
                AssigneeIssueVO assigneeIssueVO = new AssigneeIssueVO();
                BeanUtils.copyProperties(assigneeIssueDO, assigneeIssueVO);
                assigneeIssueVO.setAssigneeName(assigneeName);
                assigneeIssueVO.setAssigneeLoginName(assigneeLoginName);
                assigneeIssueVO.setAssigneeRealName(assigneeRealName);
                assigneeIssueVO.setImageUrl(imageUrl);
                assigneeIssues.add(assigneeIssueVO);
            });
            return assigneeIssues;
        } else {
            return new ArrayList<>();
        }

    }

}
