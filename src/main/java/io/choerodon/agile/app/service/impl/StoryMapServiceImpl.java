package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.api.dto.StoryMapDragDTO;
import io.choerodon.agile.api.validator.StoryMapValidator;
import io.choerodon.agile.app.service.StoryMapService;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.infra.dataobject.EpicWithFeatureDO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.StoryMapMapper;
import io.choerodon.agile.infra.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @Override
    public JSONObject queryStoryMap(Long projectId, Long organizationId) {
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

        JSONObject result = new JSONObject(true);
        if (epicIds.isEmpty()) {
            return result;
        }
        List<EpicWithFeatureDO> epicWithFeatureDOList = storyMapMapper.selectEpicWithFeatureList(epicIds);
        result.put("epicWithFeature", epicWithFeatureDOList);
        List<Long> featureIds = new ArrayList<>();
        epicWithFeatureDOList.forEach(epicWithFeatureDO -> {
            List<FeatureCommonDO> featureCommonDOList = epicWithFeatureDO.getFeatureCommonDOList();
            featureIds.addAll(featureCommonDOList.stream().map(FeatureCommonDO::getIssueId).collect(Collectors.toList()));
        });
        result.put("storyList", storyMapMapper.selectStoryList(projectId, epicIds, featureIds));
        result.put("demandStoryList", storyMapMapper.selectDemandStoryList(projectId));
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
            issueRepository.batchStoryToFeature(projectId, featureId, issueIds);
        }
    }

    private void dragToVersion(Long projectId, Long versionId, StoryMapDragDTO storyMapDragDTO) {
        storyMapValidator.checkVersionExist(versionId);
        List<Long> issueIds = storyMapDragDTO.getVersionIssueIds();
        if (issueIds == null || issueIds.isEmpty()) {
            return;
        }
        if (!Objects.equals(versionId, 0L)) {
            issueRepository.batchRemoveVersion(projectId, issueIds);
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueRepository.batchIssueToVersion(versionIssueRelE);
        } else {
            issueRepository.batchRemoveVersion(projectId, issueIds);
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
