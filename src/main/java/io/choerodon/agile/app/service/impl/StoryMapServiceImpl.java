package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.StoryMapValidator;
import io.choerodon.agile.app.assembler.StoryMapAssembler;
import io.choerodon.agile.app.service.StoryMapService;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.StoryMapMapper;
import io.choerodon.agile.infra.mapper.StoryMapWidthMapper;
import io.choerodon.agile.infra.repository.IssueRepository;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.repository.VersionIssueRelRepository;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/5/31.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class StoryMapServiceImpl implements StoryMapService {

    @Autowired
    private StoryMapMapper storyMapMapper;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private StoryMapValidator storyMapValidator;

    @Autowired
    private VersionIssueRelRepository versionIssueRelRepository;

    @Autowired
    private StoryMapWidthMapper storyMapWidthMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoryMapAssembler storyMapAssembler;


    private List<FeatureCommonDO> setFeatureWithoutEpicByProgram(Long programId, Long projectId, List<Long> featureIds) {
        List<FeatureCommonDO> result = new ArrayList<>();
        List<FeatureCommonDO> programFeatureList = storyMapMapper.selectFeatureByNoEpicByProgram(programId, projectId);
        List<FeatureCommonDO> projectFeatureList = storyMapMapper.selectFeatureByNoEpicByProject(projectId);
        if (programFeatureList != null && !programFeatureList.isEmpty()) {
            result.addAll(programFeatureList);
        }
        if (programFeatureList != null && !projectFeatureList.isEmpty()) {
            result.addAll(projectFeatureList);
        }
        Collections.sort(result, (o1, o2) -> o2.getIssueId().compareTo(o1.getIssueId()));
        featureIds.addAll(result.stream().map(FeatureCommonDO::getIssueId).collect(Collectors.toList()));
        return result;
    }

    private List<StoryMapWidthDTO> setStoryMapWidth(Long projectId) {
        List<StoryMapWidthDO> storyMapWidthDOList = storyMapWidthMapper.selectByProjectId(projectId);
        if (storyMapWidthDOList != null && !storyMapWidthDOList.isEmpty()) {
            return ConvertHelper.convertList(storyMapWidthDOList, StoryMapWidthDTO.class);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public JSONObject queryStoryMap(Long projectId, Long organizationId, SearchDTO searchDTO) {
        JSONObject result = new JSONObject(true);
        List<Long> epicIds = new ArrayList<>();
        // get program epic
        ProjectDTO program = userRepository.getGroupInfoByEnableProject(organizationId, projectId);
        if (program != null) {
            List<Long> programEpicIds = storyMapMapper.selectEpicIdsByProgram(program.getId());
            if (programEpicIds != null && !programEpicIds.isEmpty()) {
                epicIds.addAll(programEpicIds);
            }
        }
        // get project epic
        List<Long> projectEpicIds = storyMapMapper.selectEpicIdsByProject(projectId);
        if (projectEpicIds != null && !projectEpicIds.isEmpty()) {
            epicIds.addAll(projectEpicIds);
        }

        List<Long> featureIds = new ArrayList<>();
        if (epicIds.isEmpty()) {
            result.put("epicWithFeature", new ArrayList<>());
        } else {
            List<EpicWithFeatureDO> epicWithFeatureDOList = storyMapMapper.selectEpicWithFeatureList(projectId, epicIds);
            result.put("epicWithFeature", epicWithFeatureDOList);
            epicWithFeatureDOList.forEach(epicWithFeatureDO -> {
                List<FeatureCommonDO> featureCommonDOList = epicWithFeatureDO.getFeatureCommonDOList();
                featureIds.addAll(featureCommonDOList.stream().map(FeatureCommonDO::getIssueId).collect(Collectors.toList()));
            });
        }
        if (program != null) {
            result.put("featureWithoutEpic", setFeatureWithoutEpicByProgram(program.getId(), projectId, featureIds));
        } else {
            List<FeatureCommonDO> featureCommonDOList = storyMapMapper.selectFeatureByNoEpicByProject(projectId);
            featureIds.addAll(featureCommonDOList.stream().map(FeatureCommonDO::getIssueId).collect(Collectors.toList()));
            result.put("featureWithoutEpic", featureCommonDOList);
        }

        result.put("storyList", !epicIds.isEmpty() || !featureIds.isEmpty() ? storyMapMapper.selectStoryList(projectId, epicIds, featureIds, searchDTO) : new ArrayList<>());
        result.put("storyMapWidth", setStoryMapWidth(projectId));
        return result;
    }

    @Override
    public JSONObject queryStoryMapDemand(Long projectId, SearchDTO searchDTO) {
        JSONObject result = new JSONObject(true);
        List<StoryMapStoryDO> storyMapStoryDOList = storyMapMapper.selectDemandStoryList(projectId, searchDTO);
        result.put("demandStoryList", storyMapAssembler.storyMapStoryDOToDTO(projectId, storyMapStoryDOList));
        return result;
    }

    private void dragToEpic(Long projectId, Long epicId, StoryMapDragDTO storyMapDragDTO) {
        storyMapValidator.checkEpicExist(epicId);
        List<Long> issueIds = storyMapDragDTO.getEpicIssueIds();
        if (issueIds != null && !issueIds.isEmpty()) {
            issueRepository.batchIssueToEpic(projectId, epicId, issueIds);
        }
    }

    private void dragToFeature(Long projectId, Long featureId, StoryMapDragDTO storyMapDragDTO) {
        storyMapValidator.checkFeatureExist(featureId);
        List<Long> issueIds = storyMapDragDTO.getFeatureIssueIds();
        if (issueIds != null && !issueIds.isEmpty()) {
            issueRepository.batchStoryToFeature(projectId, featureId, issueIds, null);
        }
    }

    private void dragToVersion(Long projectId, Long versionId, StoryMapDragDTO storyMapDragDTO) {
        List<VersionIssueRelDTO> versionIssueRelDTOList = storyMapDragDTO.getVersionIssueRelDTOList();
        if (versionIssueRelDTOList != null && !versionIssueRelDTOList.isEmpty()) {
            for (VersionIssueRelDTO versionIssueRelDTO : versionIssueRelDTOList) {
                VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
                versionIssueRelDO.setIssueId(versionIssueRelDTO.getIssueId());
                versionIssueRelDO.setVersionId(versionIssueRelDTO.getVersionId());
                versionIssueRelDO.setRelationType("fix");
                versionIssueRelDO.setProjectId(projectId);
                versionIssueRelRepository.delete(versionIssueRelDO);
            }
        }
        storyMapValidator.checkVersionExist(versionId);
        List<Long> issueIds = storyMapDragDTO.getVersionIssueIds();
        if (issueIds == null || issueIds.isEmpty()) {
            return;
        }
        if (!Objects.equals(versionId, 0L)) {
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueRepository.batchIssueToVersion(versionIssueRelE);
        }
    }

    @Override
    public void storyMapMove(Long projectId, StoryMapDragDTO storyMapDragDTO) {
        Long epicId = storyMapDragDTO.getEpicId();
        Long featureId = storyMapDragDTO.getFeatureId();
        Long versionId = storyMapDragDTO.getVersionId();
        // 排除featureId不在epicId下的情况
        storyMapValidator.checkFeatureUnderEpic(featureId, epicId);
        if (epicId != null) {
            dragToEpic(projectId, epicId, storyMapDragDTO);
        }
        if (featureId != null) {
            dragToFeature(projectId, featureId, storyMapDragDTO);
        }
        if (versionId != null) {
            dragToVersion(projectId, versionId, storyMapDragDTO);
        }
    }

}
