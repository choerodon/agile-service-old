package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.AssigneeIssueDTO;
import io.choerodon.agile.infra.dataobject.IssueSearchDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
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

    public List<IssueSearchVO> doListToDTO(List<IssueSearchDTO> issueSearchDTOList, Map<Long, UserMessageDTO> usersMap, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        if (issueSearchDTOList != null && !issueSearchDTOList.isEmpty()) {
            List<IssueSearchVO> issueSearchVOList = new ArrayList<>(issueSearchDTOList.size());
            issueSearchDTOList.forEach(issueSearch -> {
                UserMessageDTO userMessageDTO = usersMap.get(issueSearch.getAssigneeId());
                String assigneeName = userMessageDTO != null ? userMessageDTO.getName() : null;
                String imageUrl = assigneeName != null ? userMessageDTO.getImageUrl() : null;
                String assigneeLoginName = userMessageDTO != null ? userMessageDTO.getLoginName() : null;
                String assigneeRealName = userMessageDTO != null ? userMessageDTO.getRealName() : null;
                issueSearch.setAssigneeName(assigneeName);
                issueSearch.setImageUrl(imageUrl);
                issueSearch.setAssigneeLoginName(assigneeLoginName);
                issueSearch.setAssigneeRealName(assigneeRealName);
                issueSearch.setPriorityVO(priorityMap.get(issueSearch.getPriorityId()));
                issueSearch.setStatusMapVO(statusMapDTOMap.get(issueSearch.getStatusId()));
                issueSearch.setIssueTypeVO(issueTypeDTOMap.get(issueSearch.getIssueTypeId()));
                issueSearchVOList.add(toTarget(issueSearch, IssueSearchVO.class));
            });
            return issueSearchVOList;
        } else {
            return new ArrayList<>();
        }
    }

    public List<AssigneeIssueVO> doListToAssigneeIssueDTO(List<AssigneeIssueDTO> assigneeIssueDTOList, Map<Long, UserMessageDTO> usersMap) {
        if (assigneeIssueDTOList != null && !assigneeIssueDTOList.isEmpty()) {
            List<AssigneeIssueVO> assigneeIssues = new ArrayList<>(assigneeIssueDTOList.size());
            assigneeIssueDTOList.forEach(assigneeIssueDO -> {
                UserMessageDTO userMessageDTO = usersMap.get(assigneeIssueDO.getAssigneeId());
                String assigneeName = userMessageDTO != null ? userMessageDTO.getName() : null;
                String assigneeLoginName = userMessageDTO != null ? userMessageDTO.getLoginName() : null;
                String assigneeRealName = userMessageDTO != null ? userMessageDTO.getRealName() : null;
                String imageUrl = assigneeName != null ? userMessageDTO.getImageUrl() : null;
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
