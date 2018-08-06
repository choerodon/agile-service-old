package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.SprintDetailDTO;
import io.choerodon.agile.api.dto.SprintSearchDTO;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.dataobject.SprintSearchDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
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
public class SprintSearchAssembler {

    @Autowired
    private IssueSearchAssembler issueSearchAssembler;

    public SprintE doToEntity(SprintDO sprintDO) {
        SprintE sprintE = new SprintE();
        BeanUtils.copyProperties(sprintDO, sprintE);
        return sprintE;
    }

    public SprintSearchDTO doToDTO(SprintSearchDO sprintSearchDO,Map<Long, UserMessageDO> usersMap) {
        if (sprintSearchDO == null) {
            return null;
        }
        SprintSearchDTO sprintSearchDTO = new SprintSearchDTO();
        BeanUtils.copyProperties(sprintSearchDO, sprintSearchDTO);
        sprintSearchDTO.setAssigneeIssues(issueSearchAssembler.doListToAssigneeIssueDTO(sprintSearchDO.getAssigneeIssueDOList(),usersMap));
        sprintSearchDTO.setIssueSearchDTOList(issueSearchAssembler.doListToDTO(sprintSearchDO.getIssueSearchDOList(), usersMap));
        return sprintSearchDTO;
    }

    public List<SprintSearchDTO> doListToDTO(List<SprintSearchDO> sprintSearchDOS,Map<Long, UserMessageDO> usersMap) {
        if (sprintSearchDOS == null) {
            return new ArrayList<>();
        }
        List<SprintSearchDTO> sprintSearchList = new ArrayList<>();
        sprintSearchDOS.forEach(sprintSearchDO -> sprintSearchList.add(doToDTO(sprintSearchDO,usersMap)));
        return sprintSearchList;
    }

    public SprintDetailDTO doToDetailDTO(SprintDO sprintDO) {
        if(sprintDO == null){
            return null;
        }
        SprintDetailDTO sprintDetailDTO = new SprintDetailDTO();
        BeanUtils.copyProperties(sprintDO, sprintDetailDTO);
        return sprintDetailDTO;
    }
}
