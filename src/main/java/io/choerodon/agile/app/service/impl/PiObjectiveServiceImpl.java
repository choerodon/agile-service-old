package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.PiObjectiveDTO;
import io.choerodon.agile.api.vo.ProjectRelationshipDTO;
import io.choerodon.agile.app.service.PiObjectiveService;
import io.choerodon.agile.domain.agile.entity.PiObjectiveE;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.repository.PiObjectiveRepository;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.PiObjectiveDO;
import io.choerodon.agile.infra.mapper.PiObjectiveMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PiObjectiveServiceImpl implements PiObjectiveService {

    @Autowired
    private PiObjectiveRepository piObjectiveRepository;

    @Autowired
    private PiObjectiveMapper piObjectiveMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public PiObjectiveDTO createPiObjective(Long programId, PiObjectiveDTO piObjectiveDTO) {
        return ConvertHelper.convert(piObjectiveRepository.create(ConvertHelper.convert(piObjectiveDTO, PiObjectiveE.class)), PiObjectiveDTO.class);
    }

    @Override
    public PiObjectiveDTO updatePiObjective(Long programId, PiObjectiveDTO piObjectiveDTO) {
        return ConvertHelper.convert(piObjectiveRepository.updateBySelective(ConvertHelper.convert(piObjectiveDTO, PiObjectiveE.class)), PiObjectiveDTO.class);
    }

    @Override
    public void deletePiObjective(Long programId, Long id) {
        piObjectiveRepository.delete(id);
    }

    @Override
    public PiObjectiveDTO queryPiObjective(Long programId, Long id) {
        PiObjectiveDO piObjectiveDO = piObjectiveMapper.selectByPrimaryKey(id);
        if (piObjectiveDO == null) {
            throw new CommonException("error.piObjective.select");
        }
        return ConvertHelper.convert(piObjectiveDO, PiObjectiveDTO.class);
    }

    @Override
    public JSONObject queryPiObjectiveList(Long programId, Long piId) {
        JSONObject result = new JSONObject();
        // get team
        List<ProjectRelationshipDTO> projectRelationshipDTOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        List<Long> teamWithProgramIds = new ArrayList<>();
        teamWithProgramIds.add(programId);
        if (projectRelationshipDTOList != null && !projectRelationshipDTOList.isEmpty()) {
            teamWithProgramIds.addAll(projectRelationshipDTOList.stream().map(ProjectRelationshipDTO::getProjectId).collect(Collectors.toList()));
        }
        List<PiObjectiveDO> piObjectiveDOList = piObjectiveMapper.selectPiObjectiveList(programId, piId, teamWithProgramIds);
        List<PiObjectiveDO> programPiObjectives = new ArrayList<>();
        Map<Long, List<PiObjectiveDO>> map = new HashMap<>();
        for (PiObjectiveDO piObjectiveDO : piObjectiveDOList) {
            if ("program".equals(piObjectiveDO.getLevelCode())) {
                programPiObjectives.add(piObjectiveDO);
            } else if ("team".equals(piObjectiveDO.getLevelCode())) {
                Long projectId = piObjectiveDO.getProjectId();
                if (map.get(projectId) == null) {
                    List<PiObjectiveDO> projectPiObjectives = new ArrayList<>();
                    projectPiObjectives.add(piObjectiveDO);
                    map.put(projectId, projectPiObjectives);
                } else {
                    List<PiObjectiveDO> projectPiObjectives = map.get(projectId);
                    projectPiObjectives.add(piObjectiveDO);
                    map.put(projectId, projectPiObjectives);
                }
            }
        }
        JSONObject projectJson = new JSONObject();
        Iterator<Map.Entry<Long, List<PiObjectiveDO>>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Long, List<PiObjectiveDO>> entry = entries.next();
            Long key = entry.getKey();
            List<PiObjectiveDO> piObjectiveDOS = entry.getValue();
            String projectName = ConvertUtil.getName(key);
            projectJson.put(projectName, piObjectiveDOS);
        }
        result.put("program", programPiObjectives);
        result.put("team", projectJson);
        return result;
    }

    @Override
    public List<PiObjectiveDTO> queryPiObjectiveListByProject(Long projectId, Long piId) {
        List<PiObjectiveDO> piObjectiveDOList = piObjectiveMapper.selectPiObjectiveListByProject(projectId, piId);
        if (piObjectiveDOList != null && !piObjectiveDOList.isEmpty()) {
            return ConvertHelper.convertList(piObjectiveDOList, PiObjectiveDTO.class);
        } else {
            return new ArrayList<>();
        }
    }
}
