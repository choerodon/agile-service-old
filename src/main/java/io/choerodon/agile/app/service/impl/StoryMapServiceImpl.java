package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.api.dto.StoryMapDragDTO;
import io.choerodon.agile.api.dto.VersionIssueRelDTO;
import io.choerodon.agile.api.dto.StoryMapWidthDTO;
import io.choerodon.agile.api.validator.StoryMapValidator;
import io.choerodon.agile.app.service.StoryMapService;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.infra.dataobject.EpicWithFeatureDO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDO;
import io.choerodon.agile.infra.dataobject.VersionIssueRelDO;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.StoryMapWidthDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.StoryMapMapper;
import io.choerodon.agile.infra.mapper.StoryMapWidthMapper;
import io.choerodon.agile.infra.repository.IssueRepository;
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
    private UserFeignClient userFeignClient;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private StoryMapValidator storyMapValidator;

    @Autowired
    private VersionIssueRelRepository versionIssueRelRepository;

    @Autowired
    private StoryMapWidthMapper storyMapWidthMapper;

    @Autowired
    private IssueMapper issueMapper;


    private List<FeatureCommonDO> setFeatureWithoutEpicByProgram(Long programId, Long projectId) {
        List<FeatureCommonDO> result = new ArrayList<>();
        List<FeatureCommonDO> programFeatureList = storyMapMapper.selectFeatureByNoEpicByProgram(programId);
        List<FeatureCommonDO> projectFeatureList = storyMapMapper.selectFeatureByNoEpicByProject(projectId);
        if (programFeatureList != null && !programFeatureList.isEmpty()) {
            result.addAll(programFeatureList);
        }
        if (programFeatureList != null && !projectFeatureList.isEmpty()) {
            result.addAll(projectFeatureList);
        }
        Collections.sort(result, Comparator.comparing(FeatureCommonDO::getIssueId));
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
    public JSONObject queryStoryMap(Long projectId, Long organizationId) {
        JSONObject result = new JSONObject(true);
        List<Long> epicIds = new ArrayList<>();
        // get program epic
        ProjectDTO program = userFeignClient.getGroupInfoByEnableProject(organizationId, projectId).getBody();
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

        List<EpicWithFeatureDO> epicWithFeatureDOList = storyMapMapper.selectEpicWithFeatureList(epicIds);
        result.put("epicWithFeature", epicWithFeatureDOList);
        if (program != null) {
            result.put("featureWithoutEpic", setFeatureWithoutEpicByProgram(program.getId(), projectId));
        } else {
            result.put("featureWithoutEpic", storyMapMapper.selectFeatureByNoEpicByProject(projectId));
        }

        List<Long> featureIds = new ArrayList<>();
        epicWithFeatureDOList.forEach(epicWithFeatureDO -> {
            List<FeatureCommonDO> featureCommonDOList = epicWithFeatureDO.getFeatureCommonDOList();
            featureIds.addAll(featureCommonDOList.stream().map(FeatureCommonDO::getIssueId).collect(Collectors.toList()));
        });
        result.put("storyList", storyMapMapper.selectStoryList(projectId, epicIds, featureIds));
        result.put("demandStoryList", storyMapMapper.selectDemandStoryList(projectId));
        result.put("storyMapWidth", setStoryMapWidth(projectId));
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
            IssueDO feature = issueMapper.selectByPrimaryKey(featureId);
            Long updateEpicId = (feature.getEpicId() == null ? 0L : feature.getEpicId());
            issueRepository.batchStoryToFeature(projectId, featureId, issueIds, updateEpicId);
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
