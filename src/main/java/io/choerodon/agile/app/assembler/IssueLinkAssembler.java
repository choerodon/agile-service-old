package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.IssueLinkVO;
import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.IssueLinkDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
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
    private UserService userService;

    public List<IssueLinkVO> issueLinkDTOToVO(Long projectId, List<IssueLinkDTO> issueLinkDTOList) {
        List<IssueLinkVO> issueLinkVOList = new ArrayList<>(issueLinkDTOList.size());
        if (!issueLinkDTOList.isEmpty()) {
            Map<Long, IssueTypeVO> testIssueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.TEST);
            Map<Long, IssueTypeVO> agileIssueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            List<Long> assigneeIds = issueLinkDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueLinkDTO::getAssigneeId).distinct().collect(Collectors.toList());
            Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(assigneeIds, true);
            issueLinkDTOList.forEach(issueLinkDO -> {
                String assigneeName = usersMap.get(issueLinkDO.getAssigneeId()) != null ? usersMap.get(issueLinkDO.getAssigneeId()).getName() : null;
                String imageUrl = assigneeName != null ? usersMap.get(issueLinkDO.getAssigneeId()).getImageUrl() : null;
                IssueLinkVO issueLinkVO = new IssueLinkVO();
                BeanUtils.copyProperties(issueLinkDO, issueLinkVO);
                if (issueLinkDO.getApplyType().equals(SchemeApplyType.TEST)) {
                    issueLinkVO.setIssueTypeVO(testIssueTypeDTOMap.get(issueLinkDO.getIssueTypeId()));
                } else {
                    issueLinkVO.setIssueTypeVO(agileIssueTypeDTOMap.get(issueLinkDO.getIssueTypeId()));
                }
                issueLinkVO.setStatusMapVO(statusMapDTOMap.get(issueLinkDO.getStatusId()));
                issueLinkVO.setPriorityVO(priorityDTOMap.get(issueLinkDO.getPriorityId()));
                issueLinkVO.setAssigneeName(assigneeName);
                issueLinkVO.setImageUrl(imageUrl);
                issueLinkVOList.add(issueLinkVO);
            });
        }
        return issueLinkVOList;
    }
}
