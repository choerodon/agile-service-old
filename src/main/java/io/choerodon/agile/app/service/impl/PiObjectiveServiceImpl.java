package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.PiObjectiveVO;
import io.choerodon.agile.api.vo.ProjectRelationshipVO;
import io.choerodon.agile.app.service.PiObjectiveService;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.PiObjectiveDTO;
import io.choerodon.agile.infra.mapper.PiObjectiveMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PiObjectiveServiceImpl implements PiObjectiveService {

//    @Autowired
//    private PiObjectiveRepository piObjectiveRepository;

    @Autowired
    private PiObjectiveMapper piObjectiveMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public PiObjectiveDTO createPiObjective(Long programId, PiObjectiveDTO piObjectiveDTO) {
        if (piObjectiveMapper.insert(piObjectiveDTO) != 1) {
            throw new CommonException("error.piObjective.insert");
        }
        return piObjectiveMapper.selectByPrimaryKey(piObjectiveDTO.getId());
    }

    @Override
    public PiObjectiveDTO updatePiObjective(Long programId, PiObjectiveDTO piObjectiveDTO) {
        if (piObjectiveMapper.updateByPrimaryKeySelective(piObjectiveDTO) != 1) {
            throw new CommonException("error.piObjective.insert");
        }
        return piObjectiveMapper.selectByPrimaryKey(piObjectiveDTO.getId());
    }

    @Override
    public void deletePiObjective(Long programId, Long id) {
        PiObjectiveDTO piObjectiveDTO = piObjectiveMapper.selectByPrimaryKey(id);
        if (piObjectiveDTO == null) {
            throw new CommonException("error.piObjective.exist");
        }
        if (piObjectiveMapper.delete(piObjectiveDTO) != 1) {
            throw new CommonException("error.piObjective.delete");
        }
    }

    @Override
    public PiObjectiveVO queryPiObjective(Long programId, Long id) {
        PiObjectiveDTO piObjectiveDTO = piObjectiveMapper.selectByPrimaryKey(id);
        if (piObjectiveDTO == null) {
            throw new CommonException("error.piObjective.select");
        }
        return modelMapper.map(piObjectiveDTO, PiObjectiveVO.class);
    }

    @Override
    public JSONObject queryPiObjectiveList(Long programId, Long piId) {
        JSONObject result = new JSONObject();
        // get team
        List<ProjectRelationshipVO> projectRelationshipVOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        List<Long> teamWithProgramIds = new ArrayList<>();
        teamWithProgramIds.add(programId);
        if (projectRelationshipVOList != null && !projectRelationshipVOList.isEmpty()) {
            teamWithProgramIds.addAll(projectRelationshipVOList.stream().map(ProjectRelationshipVO::getProjectId).collect(Collectors.toList()));
        }
        List<PiObjectiveDTO> piObjectiveDTOList = piObjectiveMapper.selectPiObjectiveList(programId, piId, teamWithProgramIds);
        List<PiObjectiveDTO> programPiObjectives = new ArrayList<>();
        Map<Long, List<PiObjectiveDTO>> map = new HashMap<>();
        for (PiObjectiveDTO piObjectiveDTO : piObjectiveDTOList) {
            if ("program".equals(piObjectiveDTO.getLevelCode())) {
                programPiObjectives.add(piObjectiveDTO);
            } else if ("team".equals(piObjectiveDTO.getLevelCode())) {
                Long projectId = piObjectiveDTO.getProjectId();
                if (map.get(projectId) == null) {
                    List<PiObjectiveDTO> projectPiObjectives = new ArrayList<>();
                    projectPiObjectives.add(piObjectiveDTO);
                    map.put(projectId, projectPiObjectives);
                } else {
                    List<PiObjectiveDTO> projectPiObjectives = map.get(projectId);
                    projectPiObjectives.add(piObjectiveDTO);
                    map.put(projectId, projectPiObjectives);
                }
            }
        }
        JSONObject projectJson = new JSONObject();
        Iterator<Map.Entry<Long, List<PiObjectiveDTO>>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Long, List<PiObjectiveDTO>> entry = entries.next();
            Long key = entry.getKey();
            List<PiObjectiveDTO> piObjectiveDTOS = entry.getValue();
            String projectName = ConvertUtil.getName(key);
            projectJson.put(projectName, piObjectiveDTOS);
        }
        result.put("program", programPiObjectives);
        result.put("team", projectJson);
        return result;
    }

    @Override
    public List<PiObjectiveVO> queryPiObjectiveListByProject(Long projectId, Long piId) {
        List<PiObjectiveDTO> piObjectiveDTOList = piObjectiveMapper.selectPiObjectiveListByProject(projectId, piId);
        if (piObjectiveDTOList != null && !piObjectiveDTOList.isEmpty()) {
            return modelMapper.map(piObjectiveDTOList, new TypeToken<PiObjectiveVO>(){}.getType());
        } else {
            return new ArrayList<>();
        }
    }
}
