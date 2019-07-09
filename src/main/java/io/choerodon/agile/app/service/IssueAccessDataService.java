package io.choerodon.agile.app.service;

import io.choerodon.agile.domain.agile.entity.BatchRemovePiE;
import io.choerodon.agile.domain.agile.entity.BatchRemoveSprintE;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.agile.infra.dataobject.MoveIssueDO;
import io.choerodon.agile.infra.dataobject.StoryMapMoveIssueDO;

import java.util.Date;
import java.util.List;

public interface IssueAccessDataService {

    /**
     * 按照字段更新敏捷开发Issue部分字段
     *
     * @param issueConvertDTO    issueConvertDTO
     * @param fieldList fieldList
     * @return IssueConvertDTO
     */
    IssueConvertDTO update(IssueConvertDTO issueConvertDTO, String[] fieldList);

    /**
     * 添加一个敏捷开发Issue
     *
     * @param issueConvertDTO issueConvertDTO
     * @return IssueConvertDTO
     */
    IssueConvertDTO create(IssueConvertDTO issueConvertDTO);

    /**
     * 根据id删除敏捷开发Issue
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return int
     */
    int delete(Long projectId, Long issueId);

    Boolean batchRemoveFromSprint(Long projectId, Long sprintId);

    Boolean batchIssueToVersion(VersionIssueRelE versionIssueRelE);

    Boolean batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds);

    Boolean batchStoryToFeature(Long projectId, Long featureId, List<Long> issueIds, Long epicId);

    Integer batchRemoveVersion(Long projectId, List<Long> issueIds);

    Integer batchRemoveVersionTest(Long projectId, List<Long> issueIds);

    /**
     * 将该epic下的issue的epicId设为0
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return int
     */
    int batchUpdateIssueEpicId(Long projectId, Long issueId);

    int issueToDestinationByIds(Long projectId, Long sprintId, List<Long> issueIds, Date date, Long userId);

    int batchUpdateIssueRank(Long projectId, List<MoveIssueDO> moveIssueDOS);

    int batchUpdateFeatureRank(Long projectId, List<MoveIssueDO> moveIssueDOS);

    int batchUpdateMapIssueRank(Long projectId, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS);

    int removeIssueFromSprintByIssueIds(BatchRemoveSprintE batchRemoveSprintE);

    int removeFeatureFromPiByIssueIds(BatchRemovePiE batchRemovePiE);

    int deleteIssueFromSprintByIssueId(Long projectId, Long issueId);

    /**
     * 批量更新epic的排序
     *
     * @param sequence  sequence
     * @param projectId projectId
     * @param add       add
     * @param issueId   issueId
     * @return int
     */
    int batchUpdateSequence(Integer sequence, Long projectId, Integer add, Long issueId);

    IssueConvertDTO updateSelective(IssueConvertDTO issueConvertDTO);

    int issueToDestinationByIdsCloseSprint(Long projectId, Long targetSprintId, List<Long> issueIds, Date date, Long userId);

    int featureToDestinationByIdsClosePi(Long programId, Long targetPiId, List<Long> issueIds, Date date, Long userId);

    /**
     * 【内部调用】状态机方案变更后批量更新issue的状态匹配
     *
     * @param projectId   projectId
     * @param applyType   applyType
     * @param issueTypeId issueTypeId
     * @param oldStatusId oldStatusId
     * @param newStatusId newStatusId
     * @param userId      userId
     */
    void updateIssueStatusByIssueTypeId(Long projectId, String applyType, Long issueTypeId, Long oldStatusId, Long newStatusId, Long userId);

    void updateStayDate(Long projectId, Long sprintId, Date nowDate);

    /**
     * 【内部调用】批量更新issue的优先级
     *
     * @param organizationId
     * @param priorityId
     * @param changePriorityId
     * @param projectIds
     */
    void batchUpdateIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds);

    void batchFeatureToPi(Long programId, Long piId, List<Long> issueIds, Date date, Long userId);

    void batchFeatureToEpic(Long programId, Long epicId, List<Long> featureIds);

    void updateEpicIdOfStoryByFeature(Long featureId, Long epicId);

    void updateEpicIdOfStoryByFeatureList(List<Long> featureIds, Long epicId);

    void updateStatusIdBatch(Long programId, Long updateStatusId, List<IssueDTO> issueDTOList, Long lastUpdatedBy, Date lastUpdateDate);
}
