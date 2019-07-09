package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.StoryMapValidator;
import io.choerodon.agile.app.assembler.StoryMapAssembler;
import io.choerodon.agile.app.service.StoryMapService;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.StoryMapMapper;
import io.choerodon.agile.infra.mapper.StoryMapWidthMapper;
import io.choerodon.agile.infra.repository.IssueRepository;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.app.service.VersionIssueRelService;
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
    private VersionIssueRelService versionIssueRelService;

    @Autowired
    private StoryMapWidthMapper storyMapWidthMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private StoryMapAssembler storyMapAssembler;


    private List<FeatureCommonDTO> setFeatureWithoutEpicByProgram(Long programId, Long projectId, List<Long> featureIds) {
        List<FeatureCommonDTO> result = new ArrayList<>();
        List<FeatureCommonDTO> programFeatureList = storyMapMapper.selectFeatureByNoEpicByProgram(programId, projectId);
        List<FeatureCommonDTO> projectFeatureList = storyMapMapper.selectFeatureByNoEpicByProject(projectId);
        if (programFeatureList != null && !programFeatureList.isEmpty()) {
            result.addAll(programFeatureList);
        }
        if (programFeatureList != null && !projectFeatureList.isEmpty()) {
            result.addAll(projectFeatureList);
        }
        Collections.sort(result, (o1, o2) -> o2.getIssueId().compareTo(o1.getIssueId()));
        featureIds.addAll(result.stream().map(FeatureCommonDTO::getIssueId).collect(Collectors.toList()));
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
    public JSONObject queryStoryMap(Long projectId, Long organizationId, SearchVO searchVO) {
        JSONObject result = new JSONObject(true);
        List<Long> epicIds = new ArrayList<>();
        // get program epic
        ProjectDTO program = userService.getGroupInfoByEnableProject(organizationId, projectId);
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
                List<FeatureCommonDTO> featureCommonDTOList = epicWithFeatureDO.getFeatureCommonDTOList();
                featureIds.addAll(featureCommonDTOList.stream().map(FeatureCommonDTO::getIssueId).collect(Collectors.toList()));
            });
        }
        if (program != null) {
            result.put("featureWithoutEpic", setFeatureWithoutEpicByProgram(program.getId(), projectId, featureIds));
        } else {
            List<FeatureCommonDTO> featureCommonDTOList = storyMapMapper.selectFeatureByNoEpicByProject(projectId);
            featureIds.addAll(featureCommonDTOList.stream().map(FeatureCommonDTO::getIssueId).collect(Collectors.toList()));
            result.put("featureWithoutEpic", featureCommonDTOList);
        }

        result.put("storyList", !epicIds.isEmpty() || !featureIds.isEmpty() ? storyMapMapper.selectStoryList(projectId, epicIds, featureIds, searchVO) : new ArrayList<>());
        result.put("storyMapWidth", setStoryMapWidth(projectId));
        return result;
    }

    @Override
    public JSONObject queryStoryMapDemand(Long projectId, SearchVO searchVO) {
        JSONObject result = new JSONObject(true);
        List<StoryMapStoryDO> storyMapStoryDOList = storyMapMapper.selectDemandStoryList(projectId, searchVO);
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
                versionIssueRelService.delete(versionIssueRelDO);
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
