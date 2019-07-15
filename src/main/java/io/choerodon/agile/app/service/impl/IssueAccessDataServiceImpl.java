package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.IssueAccessDataService;
import io.choerodon.agile.infra.dataobject.BatchRemovePiDTO;
import io.choerodon.agile.infra.dataobject.BatchRemoveSprintDTO;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.entity.Criteria;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class IssueAccessDataServiceImpl implements IssueAccessDataService {

    private static final String UPDATE_ERROR = "error.Issue.update";
    private static final String INSERT_ERROR = "error.Issue.create";
    private static final String DELETE_ERROR = "error.Issue.delete";

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private RedisUtil redisUtil;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    @DataLog(type = "issue")
    public IssueConvertDTO update(IssueConvertDTO issueConvertDTO, String[] fieldList) {
        IssueDTO issueDTO = modelMapper.map(issueConvertDTO, IssueDTO.class);
        Criteria criteria = new Criteria();
        criteria.update(fieldList);
        if (issueMapper.updateByPrimaryKeyOptions(issueDTO, criteria) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return modelMapper.map(issueMapper.selectByPrimaryKey(issueDTO.getIssueId()), IssueConvertDTO.class);
    }

    @Override
    @DataLog(type = "issueCreate")
    public IssueConvertDTO create(IssueConvertDTO issueConvertDTO) {
        //临时存个优先级code
        issueConvertDTO.setPriorityCode("priority-" + issueConvertDTO.getPriorityId());
        IssueDTO issueDTO = modelMapper.map(issueConvertDTO, IssueDTO.class);
        if (issueMapper.insert(issueDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return modelMapper.map(issueMapper.selectByPrimaryKey(issueDTO.getIssueId()), IssueConvertDTO.class);
    }

    @Override
    public int delete(Long projectId, Long issueId) {
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setProjectId(projectId);
        issueDTO.setIssueId(issueId);
        IssueDTO issueDTO1 = issueMapper.selectOne(issueDTO);
        int isDelete = issueMapper.delete(issueDTO1);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return isDelete;
    }

    @Override
    @DataLog(type = "batchRemoveSprintBySprintId", single = false)
    public Boolean batchRemoveFromSprint(Long projectId, Long sprintId) {
        issueMapper.removeFromSprint(projectId, sprintId);
        return true;
    }

    @Override
    @DataLog(type = "batchToVersion", single = false)
    public Boolean batchIssueToVersion(VersionIssueRelDTO versionIssueRelDTO) {
        issueMapper.batchIssueToVersion(versionIssueRelDTO.getProjectId(), versionIssueRelDTO.getVersionId(), versionIssueRelDTO.getIssueIds(), versionIssueRelDTO.getCreationDate(), versionIssueRelDTO.getCreatedBy());
        return true;
    }

    @Override
    @DataLog(type = "batchToEpic", single = false)
    public Boolean batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds) {
        issueMapper.batchIssueToEpic(projectId, epicId, issueIds);
        return true;
    }

    @Override
    @DataLog(type = "batchStoryToFeature", single = false)
    public Boolean batchStoryToFeature(Long projectId, Long featureId, List<Long> issueIds, Long updateEpicId) {
        issueMapper.batchStoryToFeature(projectId, featureId, issueIds, updateEpicId);
        return true;
    }

    @Override
    @DataLog(type = "batchRemoveVersion", single = false)
    public Integer batchRemoveVersion(Long projectId, List<Long> issueIds) {
        return issueMapper.batchRemoveFromVersion(projectId, issueIds);
    }

    @Override
    @DataLog(type = "batchRemoveVersion", single = false)
    public Integer batchRemoveVersionTest(Long projectId, List<Long> issueIds) {
        return issueMapper.batchRemoveFromVersionTest(projectId, issueIds);
    }


    @Override
    @DataLog(type = "batchUpdateIssueEpicId", single = false)
    public int batchUpdateIssueEpicId(Long projectId, Long issueId) {
        redisUtil.deleteRedisCache(new String[]{"Agile:EpicChart" + projectId + ":" + issueId + ":" + "*"});
        return issueMapper.batchUpdateIssueEpicId(projectId, issueId);
    }

    @Override
    public int issueToDestinationByIds(Long projectId, Long sprintId, List<Long> issueIds, Date date, Long userId) {
        return issueMapper.issueToDestinationByIds(projectId, sprintId, issueIds, date, userId);
    }

    @Override
    public int batchUpdateIssueRank(Long projectId, List<MoveIssueDTO> moveIssues) {
        return issueMapper.batchUpdateIssueRank(projectId, moveIssues);
    }

    @Override
    public int batchUpdateFeatureRank(Long programId, List<MoveIssueDTO> moveIssues) {
        return issueMapper.batchUpdateFeatureRank(programId, moveIssues);
    }

    @Override
    public int batchUpdateMapIssueRank(Long projectId, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        return issueMapper.batchUpdateMapIssueRank(projectId, storyMapMoveIssueDOS);
    }

    @Override
    @DataLog(type = "batchRemoveSprint", single = false)
    public int removeIssueFromSprintByIssueIds(BatchRemoveSprintDTO batchRemoveSprintDTO) {
        return issueMapper.removeIssueFromSprintByIssueIds(batchRemoveSprintDTO.getProjectId(), batchRemoveSprintDTO.getIssueIds());
    }

    @Override
    @DataLog(type = "batchRemovePi", single = false)
    public int removeFeatureFromPiByIssueIds(BatchRemovePiDTO batchRemovePiDTO) {
        return issueMapper.removeFeatureFromPiByIssueIds(batchRemovePiDTO.getProgramId(), batchRemovePiDTO.getIssueIds());
    }

    @Override
    public int deleteIssueFromSprintByIssueId(Long projectId, Long issueId) {
        return issueMapper.deleteIssueFromSprintByIssueId(projectId, issueId);
    }

    @Override
    public int batchUpdateSequence(Integer sequence, Long projectId, Integer add, Long issueId) {
        return issueMapper.batchUpdateSequence(sequence, projectId, add, issueId);
    }

    @Override
    public IssueConvertDTO updateSelective(IssueConvertDTO issueConvertDTO) {
        IssueDTO issueDTO = modelMapper.map(issueConvertDTO, IssueDTO.class);
        if (issueMapper.updateByPrimaryKeySelective(issueDTO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return modelMapper.map(issueMapper.selectByPrimaryKey(issueDTO.getIssueId()), IssueConvertDTO.class);

    }

    @Override
    @DataLog(type = "batchRemoveSprintToTarget", single = false)
    public int issueToDestinationByIdsCloseSprint(Long projectId, Long targetSprintId, List<Long> issueIds, Date date, Long userId) {
        return issueMapper.issueToDestinationByIds(projectId, targetSprintId, issueIds, date, userId);
    }

    @Override
    @DataLog(type = "batchRemovePiToTarget", single = false)
    public int featureToDestinationByIdsClosePi(Long programId, Long targetPiId, List<Long> issueIds, Date date, Long userId) {
        return issueMapper.featureToDestinationByIdsClosePi(programId, targetPiId, issueIds, date, userId);
    }

    @Override
    @DataLog(type = "batchUpdateIssueStatusToOther", single = false)
    public void updateIssueStatusByIssueTypeId(Long projectId, String applyType, Long issueTypeId, Long oldStatusId, Long newStatusId, Long userId) {
        issueMapper.updateIssueStatusByIssueTypeId(projectId, applyType, issueTypeId, oldStatusId, newStatusId, userId);
    }

    @Override
    @DataLog(type = "batchUpdateIssuePriority", single = false)
    public void batchUpdateIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds) {
        System.out.println("我执行了："+organizationId+","+priorityId+","+changePriorityId+","+userId+","+projectIds);
        issueMapper.batchUpdateIssuePriority(priorityId, changePriorityId, userId, projectIds);
    }

    @Override
    public void updateStayDate(Long projectId, Long sprintId, Date nowDate) {
        issueMapper.updateStayDate(projectId, sprintId, nowDate);
    }

    @Override
    public void batchFeatureToPi(Long programId, Long piId, List<Long> issueIds, Date date, Long userId) {
        issueMapper.batchFeatureToPi(programId, piId, issueIds, date, userId);
    }

    @Override
    public void batchFeatureToEpic(Long programId, Long epicId, List<Long> featureIds) {
        issueMapper.batchFeatureToEpic(programId, epicId, featureIds);
    }

    @Override
    @DataLog(type = "updateEpicIdOfStoryByFeature", single = false)
    public void updateEpicIdOfStoryByFeature(Long featureId, Long epicId) {
        issueMapper.updateEpicIdOfStoryByFeature(featureId, epicId);
    }

    @Override
    @DataLog(type = "updateEpicIdOfStoryByFeatureList", single = false)
    public void updateEpicIdOfStoryByFeatureList(List<Long> featureIds, Long epicId) {
        issueMapper.updateEpicIdOfStoryByFeatureList(featureIds, epicId);
    }

    @Override
    @DataLog(type = "batchUpdateStatusId", single = false)
    public void updateStatusIdBatch(Long programId, Long updateStatusId, List<IssueDTO> issueDTOList, Long lastUpdatedBy, Date lastUpdateDate) {
        List<Long> issueIds = issueDTOList.stream().map(IssueDTO::getIssueId).collect(Collectors.toList());
        issueMapper.updateStatusIdBatch(programId, updateStatusId, issueIds, lastUpdatedBy, lastUpdateDate);
    }
}
