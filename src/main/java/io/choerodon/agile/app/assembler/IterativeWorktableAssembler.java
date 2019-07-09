package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.AssigneeIssueVO;
import io.choerodon.agile.infra.dataobject.AssigneeIssueDTO;
import io.choerodon.agile.infra.repository.UserRepository;
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

    public List<AssigneeIssueVO> assigneeIssueDOToDTO(List<AssigneeIssueDTO> assigneeIssueDTOList) {
        List<AssigneeIssueVO> assigneeIssueVOList = new ArrayList<>(assigneeIssueDTOList.size());
        List<Long> assigneeIds = assigneeIssueDTOList.stream().
                filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).
                map(AssigneeIssueDTO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        assigneeIssueDTOList.forEach(assigneeIssueDO -> {
            String assigneeName = usersMap.get(assigneeIssueDO.getAssigneeId()) != null ? usersMap.get(assigneeIssueDO.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(assigneeIssueDO.getAssigneeId()).getImageUrl() : null;
            AssigneeIssueVO assigneeIssueVO = new AssigneeIssueVO();
            BeanUtils.copyProperties(assigneeIssueDO, assigneeIssueVO);
            assigneeIssueVO.setImageUrl(imageUrl);
            assigneeIssueVO.setAssigneeName(assigneeName);
            assigneeIssueVOList.add(assigneeIssueVO);
        });
        return assigneeIssueVOList;
    }

}
