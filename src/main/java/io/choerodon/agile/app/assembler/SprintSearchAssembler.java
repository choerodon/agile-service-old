package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.infra.dataobject.SprintSearchDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class SprintSearchAssembler extends AbstractAssembler {

    @Autowired
    private IssueSearchAssembler issueSearchAssembler;

    public SprintSearchDTO doToDTO(SprintSearchDO sprintSearchDO, Map<Long, UserMessageDO> usersMap, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        if (sprintSearchDO == null) {
            return null;
        }
        SprintSearchDTO sprintSearchDTO = new SprintSearchDTO();
        BeanUtils.copyProperties(sprintSearchDO, sprintSearchDTO);
        if (usersMap != null) {
            sprintSearchDTO.setAssigneeIssues(issueSearchAssembler.doListToAssigneeIssueDTO(sprintSearchDO.getAssigneeIssueDOList(), usersMap));
            sprintSearchDTO.setIssueSearchDTOList(issueSearchAssembler.doListToDTO(sprintSearchDO.getIssueSearchDOList(), usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap));
        }
        return sprintSearchDTO;
    }

    public List<SprintSearchDTO> doListToDTO(List<SprintSearchDO> sprintSearchDOS, Map<Long, UserMessageDO> usersMap, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<SprintSearchDTO> sprintSearchList = new ArrayList<>(sprintSearchDOS.size());
        sprintSearchDOS.forEach(sprintSearchDO -> sprintSearchList.add(doToDTO(sprintSearchDO, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap)));
        return sprintSearchList;
    }

}
