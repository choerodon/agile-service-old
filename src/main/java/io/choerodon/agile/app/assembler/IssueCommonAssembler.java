package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IssueCommonDTO;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.common.utils.ColorUtil;
import io.choerodon.agile.infra.dataobject.IssueCommonDO;
import io.choerodon.agile.infra.dataobject.LookupValueDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.mapper.LookupValueMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/20.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueCommonAssembler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LookupValueMapper lookupValueMapper;

    private static final String ISSUE_STATUS_COLOR = "issue_status_color";

    public List<IssueCommonDTO> issueCommonToIssueCommonDto(List<IssueCommonDO> issueCommonDOList) {
        LookupValueDO lookupValueDO = new LookupValueDO();
        lookupValueDO.setTypeCode(ISSUE_STATUS_COLOR);
        List<IssueCommonDTO> issueCommonDTOList = new ArrayList<>(issueCommonDOList.size());
        Map<String, String> lookupValueMap = lookupValueMapper.select(lookupValueDO).stream().collect(Collectors.toMap(LookupValueDO::getValueCode, LookupValueDO::getName));
        List<Long> assigneeIds = issueCommonDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueCommonDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        issueCommonDOList.forEach(issueCommonDO -> {
            String assigneeName = usersMap.get(issueCommonDO.getAssigneeId()) != null ? usersMap.get(issueCommonDO.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(issueCommonDO.getAssigneeId()).getImageUrl() : null;
            IssueCommonDTO issueCommonDTO = new IssueCommonDTO();
            BeanUtils.copyProperties(issueCommonDO, issueCommonDTO);
            issueCommonDTO.setStatusColor(ColorUtil.initializationStatusColor(issueCommonDTO.getStatusCode(), lookupValueMap));
            issueCommonDTO.setAssigneeName(assigneeName);
            issueCommonDTO.setImageUrl(imageUrl);
            issueCommonDTOList.add(issueCommonDTO);
        });
        return issueCommonDTOList;
    }

}
