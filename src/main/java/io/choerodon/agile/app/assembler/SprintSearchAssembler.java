package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.SprintSearchDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class SprintSearchAssembler extends AbstractAssembler {

    @Autowired
    private IssueSearchAssembler issueSearchAssembler;

    public SprintSearchVO doToDTO(SprintSearchDTO sprintSearchDTO, Map<Long, UserMessageDTO> usersMap, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        if (sprintSearchDTO == null) {
            return null;
        }
        SprintSearchVO sprintSearchVO = new SprintSearchVO();
        BeanUtils.copyProperties(sprintSearchDTO, sprintSearchVO);
        if (usersMap != null) {
            sprintSearchVO.setAssigneeIssues(issueSearchAssembler.doListToAssigneeIssueDTO(sprintSearchDTO.getAssigneeIssueDTOList(), usersMap));
            sprintSearchVO.setIssueSearchVOList(issueSearchAssembler.doListToDTO(sprintSearchDTO.getIssueSearchDTOList(), usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap));
        }
        return sprintSearchVO;
    }

    public List<SprintSearchVO> doListToDTO(List<SprintSearchDTO> sprintSearchDTOS, Map<Long, UserMessageDTO> usersMap, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<SprintSearchVO> sprintSearchList = new ArrayList<>(sprintSearchDTOS.size());
        sprintSearchDTOS.forEach(sprintSearchDO -> sprintSearchList.add(doToDTO(sprintSearchDO, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap)));
        return sprintSearchList;
    }

}
