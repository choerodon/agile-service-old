package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.IssueLinkDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.mapper.IssueMapper;
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

    @Autowired
    private IssueMapper issueMapper;

    public List<IssueLinkDTO> issueLinkDoToDto(Long projectId, List<IssueLinkDO> issueLinkDOList) {
        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>(issueLinkDOList.size());
        if (!issueLinkDOList.isEmpty()) {
            Map<Long, IssueTypeDTO> testIssueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.TEST);
            Map<Long, IssueTypeDTO> agileIssueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            List<Long> assigneeIds = issueLinkDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueLinkDO::getAssigneeId).distinct().collect(Collectors.toList());
            Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
            issueLinkDOList.forEach(issueLinkDO -> {
                String assigneeName = usersMap.get(issueLinkDO.getAssigneeId()) != null ? usersMap.get(issueLinkDO.getAssigneeId()).getName() : null;
                String imageUrl = assigneeName != null ? usersMap.get(issueLinkDO.getAssigneeId()).getImageUrl() : null;
                IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
                BeanUtils.copyProperties(issueLinkDO, issueLinkDTO);
                if (issueLinkDO.getApplyType().equals(SchemeApplyType.TEST)) {
                    issueLinkDTO.setIssueTypeDTO(testIssueTypeDTOMap.get(issueLinkDO.getIssueTypeId()));
                } else {
                    issueLinkDTO.setIssueTypeDTO(agileIssueTypeDTOMap.get(issueLinkDO.getIssueTypeId()));
                }
                issueLinkDTO.setStatusMapDTO(statusMapDTOMap.get(issueLinkDO.getStatusId()));
                issueLinkDTO.setPriorityDTO(priorityDTOMap.get(issueLinkDO.getPriorityId()));
                issueLinkDTO.setAssigneeName(assigneeName);
                issueLinkDTO.setImageUrl(imageUrl);
                issueLinkDTOList.add(issueLinkDTO);
            });
        }
        return issueLinkDTOList;
    }
}
