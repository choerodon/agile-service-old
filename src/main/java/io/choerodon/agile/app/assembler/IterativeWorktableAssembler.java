package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.AssigneeIssueDTO;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.AssigneeIssueDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IterativeWorktableAssembler {

    @Autowired
    private UserRepository userRepository;

    public List<AssigneeIssueDTO> assigneeIssueDOToDTO(List<AssigneeIssueDO> assigneeIssueDOList) {
        List<AssigneeIssueDTO> assigneeIssueDTOList = new ArrayList<>(assigneeIssueDOList.size());
        List<Long> assigneeIds = assigneeIssueDOList.stream().
                filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).
                map(AssigneeIssueDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        assigneeIssueDOList.forEach(assigneeIssueDO -> {
            String assigneeName = usersMap.get(assigneeIssueDO.getAssigneeId()) != null ? usersMap.get(assigneeIssueDO.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(assigneeIssueDO.getAssigneeId()).getImageUrl() : null;
            AssigneeIssueDTO assigneeIssueDTO = new AssigneeIssueDTO();
            BeanUtils.copyProperties(assigneeIssueDO, assigneeIssueDTO);
            assigneeIssueDTO.setImageUrl(imageUrl);
            assigneeIssueDTO.setAssigneeName(assigneeName);
            assigneeIssueDTOList.add(assigneeIssueDTO);
        });
        return assigneeIssueDTOList;
    }

}
