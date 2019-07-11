package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.StoryMapValidator;
import io.choerodon.agile.app.assembler.StoryMapAssembler;
import io.choerodon.agile.app.service.IssueAccessDataService;
import io.choerodon.agile.app.service.StoryMapService;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.StoryMapMapper;
import io.choerodon.agile.infra.mapper.StoryMapWidthMapper;
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

//    @Autowired
//    private IssueRepository issueRepository;

    @Autowired
    private IssueAccessDataService issueAccessDataService;

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

    private List<StoryMapWidthVO> setStoryMapWidth(Long projectId) {
        List<StoryMapWidthDTO> storyMapWidthDTOList = storyMapWidthMapper.selectByProjectId(projectId);
        if (storyMapWidthDTOList != null && !storyMapWidthDTOList.isEmpty()) {
            return ConvertHelper.convertList(storyMapWidthDTOList, StoryMapWidthVO.class);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public JSONObject queryStoryMap(Long projectId, Long organizationId, SearchVO searchVO) {
        JSONObject result = new JSONObject(true);
        List<Long> epicIds = new ArrayList<>();
        // get program epic
        ProjectVO program = userService.getGroupInfoByEnableProject(organizationId, projectId);
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
            List<EpicWithFeatureDTO> epicWithFeatureDTOList = storyMapMapper.selectEpicWithFeatureList(projectId, epicIds);
            result.put("epicWithFeature", epicWithFeatureDTOList);
            epicWithFeatureDTOList.forEach(epicWithFeatureDTO -> {
                List<FeatureCommonDTO> featureCommonDTOList = epicWithFeatureDTO.getFeatureCommonDTOList();
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
        List<StoryMapStoryDTO> storyMapStoryDTOList = storyMapMapper.selectDemandStoryList(projectId, searchVO);
        result.put("demandStoryList", storyMapAssembler.storyMapStoryDOToDTO(projectId, storyMapStoryDTOList));
        return result;
    }

    private void dragToEpic(Long projectId, Long epicId, StoryMapDragVO storyMapDragVO) {
        storyMapValidator.checkEpicExist(epicId);
        List<Long> issueIds = storyMapDragVO.getEpicIssueIds();
        if (issueIds != null && !issueIds.isEmpty()) {
            issueAccessDataService.batchIssueToEpic(projectId, epicId, issueIds);
        }
    }

    private void dragToFeature(Long projectId, Long featureId, StoryMapDragVO storyMapDragVO) {
        storyMapValidator.checkFeatureExist(featureId);
        List<Long> issueIds = storyMapDragVO.getFeatureIssueIds();
        if (issueIds != null && !issueIds.isEmpty()) {
            issueAccessDataService.batchStoryToFeature(projectId, featureId, issueIds, null);
        }
    }

    private void dragToVersion(Long projectId, Long versionId, StoryMapDragVO storyMapDragVO) {
        List<VersionIssueRelVO> versionIssueRelVOList = storyMapDragVO.getVersionIssueRelVOList();
        if (versionIssueRelVOList != null && !versionIssueRelVOList.isEmpty()) {
            for (VersionIssueRelVO versionIssueRelVO : versionIssueRelVOList) {
                VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
                versionIssueRelDTO.setIssueId(versionIssueRelVO.getIssueId());
                versionIssueRelDTO.setVersionId(versionIssueRelVO.getVersionId());
                versionIssueRelDTO.setRelationType("fix");
                versionIssueRelDTO.setProjectId(projectId);
                versionIssueRelService.delete(versionIssueRelDTO);
            }
        }
        storyMapValidator.checkVersionExist(versionId);
        List<Long> issueIds = storyMapDragVO.getVersionIssueIds();
        if (issueIds == null || issueIds.isEmpty()) {
            return;
        }
        if (!Objects.equals(versionId, 0L)) {
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueAccessDataService.batchIssueToVersion(versionIssueRelE);
        }
    }

    @Override
    public void storyMapMove(Long projectId, StoryMapDragVO storyMapDragVO) {
        Long epicId = storyMapDragVO.getEpicId();
        Long featureId = storyMapDragVO.getFeatureId();
        Long versionId = storyMapDragVO.getVersionId();
        // 排除featureId不在epicId下的情况
        storyMapValidator.checkFeatureUnderEpic(featureId, epicId);
        if (epicId != null) {
            dragToEpic(projectId, epicId, storyMapDragVO);
        }
        if (featureId != null) {
            dragToFeature(projectId, featureId, storyMapDragVO);
        }
        if (versionId != null) {
            dragToVersion(projectId, versionId, storyMapDragVO);
        }
    }

}
