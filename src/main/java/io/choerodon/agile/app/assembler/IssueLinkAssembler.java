package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.IssueCommonDO;
import io.choerodon.agile.infra.dataobject.IssueLinkDO;
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
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Component
public class IssueLinkAssembler extends AbstractAssembler {

    @Autowired
    private UserRepository userRepository;

    public List<IssueLinkDTO> issueLinkDoToDto(Long projectId, List<IssueLinkDO> issueLinkDOList,String typeCode) {
        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>(issueLinkDOList.size());
        if (!issueLinkDOList.isEmpty()) {
            Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId,typeCode);
            Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            List<Long> assigneeIds = issueLinkDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueLinkDO::getAssigneeId).distinct().collect(Collectors.toList());
            Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
            issueLinkDOList.forEach(issueLinkDO -> {
                String assigneeName = usersMap.get(issueLinkDO.getAssigneeId()) != null ? usersMap.get(issueLinkDO.getAssigneeId()).getName() : null;
                IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
                BeanUtils.copyProperties(issueLinkDO, issueLinkDTO);
                issueLinkDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueLinkDO.getIssueTypeId()));
                issueLinkDTO.setStatusMapDTO(statusMapDTOMap.get(issueLinkDO.getStatusId()));
                issueLinkDTO.setPriorityDTO(priorityDTOMap.get(issueLinkDO.getPriorityId()));
                issueLinkDTO.setAssigneeName(assigneeName);
                issueLinkDTOList.add(issueLinkDTO);
            });
        }
        return issueLinkDTOList;
    }
}
