package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.IssueIdSprintIdDTO;
import io.choerodon.agile.api.vo.SearchVO;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public interface IssueMapper extends Mapper<IssueDTO> {

    int removeFromSprint(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    /**
     * 根据issueId查询issueDetail
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueDetailDTO
     */
    IssueDetailDTO queryIssueDetail(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    List<EpicDataDTO> queryEpicList(@Param("projectId") Long projectId);

    List<EpicDataDTO> queryProgramEpicList(@Param("programId") Long programid);

    int batchIssueToVersion(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("issueIds") List<Long> issueIds, @Param("date") Date date, @Param("userId") Long userId);

    int batchIssueToEpic(@Param("projectId") Long projectId, @Param("epicId") Long epicId, @Param("issueIds") List<Long> issueIds);

    int batchStoryToFeature(@Param("projectId") Long projectId, @Param("featureId") Long featureId, @Param("issueIds") List<Long> issueIds, @Param("updateEpicId") Long updateEpicId);

    List<IssueSearchDTO> queryIssueByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<Long> filterStoryIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 根据项目id查询issue中的epic
     *
     * @param projectId projectId
     * @return IssueDTO
     */
    List<IssueDTO> queryIssueEpicSelectList(@Param("projectId") Long projectId);

    List<IssueDTO> queryIssueFeatureSelectList(@Param("programId") Long programId, @Param("projectId") Long projectId, @Param("epicId") Long epicId);

    List<IssueDTO> selectFeatureListByAgileProject(@Param("projectId") Long projectId);

    List<IssueDTO> listEpicSelectProgramData(@Param("programId") Long programId);

    Integer batchRemoveFromVersion(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    Integer batchRemoveFromVersionTest(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    String queryRank(@Param("projectId") Long projectId, @Param("outsetIssueId") Long outsetIssueId);

    String queryRankByProgram(@Param("programId") Long programId, @Param("outsetIssueId") Long outsetIssueId);

    String queryLeftRankByProgram(@Param("programId") Long programId, @Param("piId") Long piId, @Param("rightRank") String rightRank);

    String queryRightRankByProgram(@Param("programId") Long programId, @Param("piId") Long piId, @Param("leftRank") String leftRank);

    List<Long> queryIssueIdOrderByRankDesc(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<Long> queryFeatureIdOrderByRankDesc(@Param("programId") Long programId, @Param("issueIds") List<Long> issueIds);

    List<Long> queryIssueIdOrderByRankAsc(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<Long> queryFeatureIdOrderByRankAsc(@Param("programId") Long programId, @Param("issueIds") List<Long> issueIds);

    String queryRightRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("leftRank") String leftRank);

    String queryLeftRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("rightRank") String rightRank);

    /**
     * 查询issue子任务列表
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueDTO
     */
    List<IssueDTO> queryIssueSubList(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    List<Long> queryIssueSubListByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    void batchDeleteIssues(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 把issueId对应的epic下的issue的epicId置为0
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return int
     */
    int batchUpdateIssueEpicId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    List<IssueCountDO> queryIssueCountByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryProgramIssueCountByEpicIds(@Param("programId") Long programId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryDoneIssueCountByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryProgramDoneIssueCountByEpicIds(@Param("programId") Long programId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryNotEstimateIssueCountByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryProgramNotEstimateIssueCountByEpicIds(@Param("programId") Long programId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryTotalEstimateByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryProgramTotalEstimateByEpicIds(@Param("programId") Long programId, @Param("epicIds") List<Long> epicIds);

    List<IssueLabelDTO> selectLabelNameByIssueId(@Param("issueId") Long issueId);

    List<Long> querySubTaskIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    int issueToDestinationByIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("issueIds") List<Long> issueIds, @Param("date") Date date, @Param("userId") Long userId);

    int featureToDestinationByIdsClosePi(@Param("programId") Long projectId, @Param("piId") Long piId, @Param("issueIds") List<Long> issueIds, @Param("date") Date date, @Param("userId") Long userId);

    int batchUpdateIssueRank(@Param("projectId") Long projectId, @Param("moveIssues") List<MoveIssueDO> moveIssues);

    int batchUpdateFeatureRank(@Param("programId") Long programId, @Param("moveIssues") List<MoveIssueDO> moveIssues);

    int batchUpdateMapIssueRank(@Param("projectId") Long projectId, @Param("storyMapMoveIssueDOS") List<StoryMapMoveIssueDO> storyMapMoveIssueDOS);

    List<Long> querySubIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    int removeIssueFromSprintByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    int removeFeatureFromPiByIssueIds(@Param("programId") Long programId, @Param("issueIds") List<Long> issueIds);

    List<Long> querySubIssueIdsByIssueId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    int deleteIssueFromSprintByIssueId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    SprintNameDTO queryActiveSprintNameByIssueId(@Param("issueId") Long issueId);

    List<SprintNameDTO> querySprintNameByIssueId(@Param("issueId") Long issueId);

    IssueDTO queryIssueSprintNotClosed(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    List queryIssueByOption(@Param("projectId") Long projectId,
                            @Param("issueId") Long issueId,
                            @Param("issueNum") String issueNum,
                            @Param("activeSprintId") Long activeSprintId,
                            @Param("self") Boolean self,
                            @Param("content") String content);

    /**
     * 根据参数查询issue列表，不对外开放
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @param issueNum  issueNum
     * @param self      self
     * @param content   content
     * @return IssueNumDTO
     */
    List<IssueNumDTO> queryIssueByOptionForAgile(@Param("projectId") Long projectId,
                                                 @Param("issueId") Long issueId,
                                                 @Param("issueNum") String issueNum,
                                                 @Param("self") Boolean self,
                                                 @Param("content") String content);

    List<ExportIssuesDO> queryExportIssues(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("projectCode") String projectCode);

    List<SprintNameDTO> querySprintNameByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<VersionIssueRelDO> queryVersionIssueRelByIssueId(@Param("issueId") Long issueId);

    List<VersionIssueRelDO> queryVersionNameByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("relationType") String relationType);

    List<LabelIssueRelDO> queryLabelIssueByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<ComponentIssueRelDO> queryComponentIssueByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 根据issueIds查询issueEpic信息
     *
     * @param projectId projectId
     * @param issueIds  issueIds
     * @return IssueDTO
     */
    List<IssueDTO> queryIssueEpicInfoByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    IssueNumDTO queryIssueByIssueNumOrIssueId(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("issueNum") String issueNum);

    List<IssueInfoDO> listByIssueIds(@Param("projectId") Long prjectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 根据参数查询issueList提供给测试模块
     *
     * @param projectId          projectId
     * @param searchArgs         searchArgs
     * @param advancedSearchArgs advancedSearchArgs
     * @param otherArgs          otherArgs
     * @param contents           contents
     * @return IssueDTO
     */
    List<IssueDTO> listIssueWithoutSubToTestComponent(@Param("projectId") Long projectId,
                                                      @Param("searchArgs") Map<String, Object> searchArgs,
                                                      @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs,
                                                      @Param("otherArgs") Map<String, Object> otherArgs,
                                                      @Param("contents") List<String> contents);

    List<IssueDTO> listIssueWithLinkedIssues(@Param("projectId") Long projectId,
                                             @Param("searchArgs") Map<String, Object> searchArgs,
                                             @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs,
                                             @Param("otherArgs") Map<String, Object> otherArgs,
                                             @Param("contents") List<String> contents);

    List<IssueCreationNumDO> queryIssueNumByTimeSlot(@Param("projectId") Long projectId,
                                                     @Param("typeCode") String typeCode,
                                                     @Param("date") Date date);

    List<Long> queryInVersionIssueIds(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("issueIds") List<Long> issueIds);

    /**
     * 查询issue和issue没有关闭的冲刺
     *
     * @param issueId issueId
     * @return IssueDTO
     */
    IssueDTO queryIssueWithNoCloseSprint(@Param("issueId") Long issueId);

    /**
     * 根据id查询epic
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return EpicDataDTO
     */
    EpicDataDTO queryEpicListByEpic(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

    /**
     * 批量更新epic的排序
     *
     * @param sequence  sequence
     * @param projectId projectId
     * @param add       add
     * @param issueId   issueId
     * @return int
     */
    int batchUpdateSequence(@Param("sequence") Integer sequence, @Param("projectId") Long projectId, @Param("add") Integer add, @Param("issueId") Long issueId);

    /**
     * 查询epic的最大排序
     *
     * @param projectId projectId
     * @return Integer
     */
    Integer queryMaxEpicSequenceByProject(@Param("projectId") Long projectId);

    /**
     * 返回issue统计信息
     *
     * @param projectId  projectId
     * @param type       type查询的类型
     * @param issueTypes issueTypes要排除的issue类型
     * @return PieChartDO
     */
    List<PieChartDO> issueStatistic(@Param("projectId") Long projectId, @Param("type") String type, @Param("issueTypes") List<String> issueTypes);

    /**
     * 返回issue的详情列表（测试模块用）
     *
     * @param issueIds issueIds
     * @return IssueComponentDetailDO
     */
    List<IssueComponentDetailDO> listIssueWithoutSubDetailByIssueIds(@Param("issueIds") List<Long> issueIds);

    /**
     * 返回issueIds（测试模块用）
     *
     * @param projectId          projectId
     * @param searchArgs         searchArgs
     * @param advancedSearchArgs advancedSearchArgs
     * @param otherArgs          otherArgs
     * @param contents           contents
     * @return IssueComponentDetailDO
     */
    List<Long> listIssueIdsWithoutSubDetail(@Param("projectId") Long projectId, @Param("searchArgs") Map<String, Object> searchArgs,
                                            @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs,
                                            @Param("otherArgs") Map<String, Object> otherArgs, @Param("contents") List<String> contents);

    /**
     * 待办事项查询相关issue的issueIds，不包含已完成的issue
     *
     * @param projectId          projectId
     * @param userId             userId
     * @param advancedSearchArgs advancedSearchArgs
     * @param filterSql          filterSql
     * @return issueIds
     */
    List<Long> querySprintIssueIdsByCondition(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs, @Param("filterSql") String filterSql, @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

    /**
     * 待办事项查询相关issue的issueIds，包含已完成的issue
     *
     * @param projectId          projectId
     * @param userId             userId
     * @param advancedSearchArgs advancedSearchArgs
     * @param filterSql          filterSql
     * @return issueIds
     */
    List<IssueIdSprintIdDTO> querySprintAllIssueIdsByCondition(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs, @Param("filterSql") String filterSql, @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

//    List<StoryMapIssueDO> listIssuesByProjectIdSprint(@Param("projectId") Long projectId,
//                                                      @Param("pageType") String pageType,
//                                                      @Param("assigneeId") Long assigneeId,
//                                                      @Param("onlyStory") Boolean onlyStory,
//                                                      @Param("filterSql") String filterSql,
//                                                      @Param("doneIds") List<Long> doneIds,
//                                                      @Param("assigneeFilterIds") List<Long> assigneeFilterIds);
//
//    List<StoryMapIssueDO> listIssuesByProjectIdVersion(@Param("projectId") Long projectId,
//                                                       @Param("pageType") String pageType,
//                                                       @Param("assigneeId") Long assigneeId,
//                                                       @Param("onlyStory") Boolean onlyStory,
//                                                       @Param("filterSql") String filterSql,
//                                                       @Param("doneIds") List<Long> doneIds,
//                                                       @Param("assigneeFilterIds") List<Long> assigneeFilterIds);
//
//    List<StoryMapIssueDO> listIssuesByProjectIdNone(@Param("projectId") Long projectId,
//                                                    @Param("pageType") String pageType,
//                                                    @Param("assigneeId") Long assigneeId,
//                                                    @Param("onlyStory") Boolean onlyStory,
//                                                    @Param("filterSql") String filterSql,
//                                                    @Param("doneIds") List<Long> doneIds,
//                                                    @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

//    List<StoryMapEpicDO> queryStoryMapEpicList(@Param("projectId") Long projectId,
//                                               @Param("showDoneEpic") Boolean showDoneEpic,
//                                               @Param("assigneeId") Long assigneeId,
//                                               @Param("onlyStory") Boolean onlyStory,
//                                               @Param("filterSql") String filterSql);

    Integer countUnResolveByProjectId(Long projectId);

    Integer countIssueByProjectId(Long projectId);

    List<Long> queryIssueIdsByOptions(@Param("projectId") Long projectId,
                                      @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs,
                                      @Param("otherArgs") Map<String, Object> otherArgs,
                                      @Param("contents") List<String> contents);

    List<UndistributedIssueDTO> queryUnDistributedIssues(Long projectId);

    List<UnfinishedIssueDO> queryUnfinishedIssues(@Param("projectId") Long projectId,
                                                  @Param("assigneeId") Long assigneeId);

    /**
     * 查询当前issue的版本关系的版本id
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return versionIds 去重后的
     */
    List<Long> queryVersionIdsByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

    /**
     * 查询epic下的所有issue
     *
     * @param projectId projectId
     * @param epicId    epicId
     * @return IssueDTO
     */
    List<IssueBurnDownReportDO> queryIssueByEpicId(@Param("projectId") Long projectId, @Param("epicId") Long epicId);

    /**
     * 查询版本下的所有issue
     *
     * @param projectId projectId
     * @param versionId versionId
     * @return IssueDTO
     */
    List<IssueBurnDownReportDO> queryIssueByVersionId(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    /**
     * 根据ids查询所有issue
     *
     * @param projectId projectId
     * @param issueIds  issueIds
     * @return IssueDTO
     */
    List<IssueDetailDTO> queryByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    String selectMinRankByProjectId(@Param("projectId") Long projectId);

    String selectMaxRankByProjectId(@Param("projectId") Long projectId);

    String selectMapRankByIssueId(@Param("projectId") Long projectId, @Param("outsetIssueId") Long outsetIssueId);

    String selectLeftMaxMapRank(@Param("projectId") Long projectId, @Param("currentMapRank") String currentMapRank);

    String selectRightMinMapRank(@Param("projectId") Long projectId, @Param("currentMapRank") String currentMapRank);

    List<Long> selectIssueIdsByProjectId(@Param("projectId") Long projectId);

    void updateMapRank(@Param("projectId") Long projectId, @Param("mapMoveIssueDOS") List<StoryMapMoveIssueDO> mapMoveIssueDOS);

    Integer queryIssueIdsIsTest(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    Integer queryIssueIdsIsNotTest(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 查询epic信息
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return IssueDTO
     */
    IssueDTO queryEpicDetailByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

    /**
     * 查询epic相关信息
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return IssueDTO
     */
    IssueDTO queryEpicWithStatusByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

    /**
     * 根据项目分组测试类型issue
     *
     * @return IssueProjectDTO
     */
    List<IssueProjectDTO> queryIssueTestGroupByProject();

    List<IssueDTO> selectAllPriority();

    List<IssueDTO> selectAllType();

    void batchUpdatePriority(@Param("issueDTOList") List<IssueDTO> issueDTOList);

    void batchUpdateIssueType(@Param("issueDTOForTypeList") List<IssueDTO> issueDTOForTypeList);

    List<Long> queryIssueIdsListWithSub(@Param("projectId") Long projectId,
                                        @Param("searchVO") SearchVO searchVO,
                                        @Param("filterSql") String filterSql,
                                        @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

    List<IssueDTO> queryIssueListWithSubByIssueIds(@Param("issueIds") List<Long> issueIds);

    /**
     * 查询issueIds对应的issueDo
     *
     * @param issueIds issueIds
     * @return IssueDTO
     */
    List<IssueDTO> queryIssueByIssueIdsAndSubIssueIds(@Param("issueIds") List<Long> issueIds);

    /**
     * 查询issueIds对应的带当前冲刺的issueDO列表
     *
     * @param projectId projectId
     * @param issueIds  issueIds
     * @return IssueDTO
     */
    List<IssueDTO> queryIssueSprintNotClosedByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 【内部接口】查询某个项目下某些应用类型处于某状态的issue有几个
     *
     * @param projectId
     * @param applyType
     * @param statusId
     * @return
     */
    Long querySizeByApplyTypeAndStatusId(@Param("projectId") Long projectId, @Param("applyType") String applyType, @Param("statusId") Long statusId);

    /**
     * 【内部接口】查询某个项目下某些问题类型下某应用类型的issue有几个
     *
     * @param projectId
     * @param applyType
     * @param issueTypeIds
     * @return
     */
    List<IssueDTO> queryByIssueTypeIdsAndApplyType(@Param("projectId") Long projectId, @Param("applyType") String applyType, @Param("issueTypeIds") List<Long> issueTypeIds);

    /**
     * 【内部调用】状态机方案变更后批量更新issue的状态匹配
     *
     * @param projectId
     * @param applyType
     * @param issueTypeId
     * @param oldStatusId
     * @param newStatusId
     */
    void updateIssueStatusByIssueTypeId(@Param("projectId") Long projectId, @Param("applyType") String applyType, @Param("issueTypeId") Long issueTypeId, @Param("oldStatusId") Long oldStatusId, @Param("newStatusId") Long newStatusId, @Param("userId") Long userId);

    /**
     * 查询某个状态下的issue信息包含是否已完成
     *
     * @param projectId   projectId
     * @param applyType   applyType
     * @param issueTypeId issueTypeId
     * @param statusId    statusId
     * @return IssueDTO
     */
    List<IssueDTO> queryIssueWithCompleteInfoByStatusId(@Param("projectId") Long projectId, @Param("applyType") String applyType, @Param("issueTypeId") Long issueTypeId, @Param("statusId") Long statusId);

    Long selectUnCloseSprintId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    void updateStayDate(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("nowDate") Date nowDate);

    void updateAssigneeIdBySpecify(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("assigneeId") Long assigneeId, @Param("creationDate") Date creationDate, @Param("lastUpdateDate") Date lastUpdateDate);

    void updateDemoCreaterBySpecify(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("userId") Long userId);

    void updateTestIssue(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("reporterId") Long reporterId, @Param("creationDate") Date creationDate, @Param("lastUpdateDate") Date lastUpdateDate);

    Long checkPriorityDelete(@Param("priorityId") Long priorityId, @Param("projectIds") List<Long> projectIds);

    void batchUpdateIssuePriority(@Param("priorityId") Long priorityId, @Param("changePriorityId") Long changePriorityId, @Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);

    List<IssueDTO> queryIssuesByPriorityId(@Param("priorityId") Long priorityId, @Param("projectIds") List<Long> projectIds);

    void batchFeatureToPi(@Param("programId") Long programId, @Param("piId") Long piId, @Param("issueIds") List<Long> issueIds, @Param("date") Date date, @Param("userId") Long userId);

    void batchFeatureToEpic(@Param("programId") Long programId, @Param("epicId") Long epicId, @Param("featureIds") List<Long> featureIds);

    List<FeatureCommonDTO> selectFeatureList(@Param("programId") Long programId, @Param("issueIds") List<Long> issueIds);

    List<Long> selectFeatureIdsByPage(@Param("programId") Long programId, @Param("searchVO") SearchVO searchVO);

    List<IssueDTO> selectFeatureByMoveIssueIds(@Param("programId") Long programId, @Param("moveIssueIdsFilter") List<Long> moveIssueIdsFilter, @Param("categoryCode") String categoryCode, @Param("piId") Long piId);

    List<IssueDTO> selectStatusChangeIssueByPiId(@Param("programId") Long programId, @Param("piId") Long piId);

    List<IssueCountDO> selectStoryCountByIds(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);

    List<IssueCountDO> selectCompletedStoryCountByIds(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);

    List<IssueCountDO> selectUnEstimateStoryCountByIds(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);

    List<IssueCountDO> selectTotalStoryPointsByIds(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);

    void updateStatusIdBatch(@Param("programId") Long programId,
                             @Param("updateStatusId") Long updateStatusId,
                             @Param("issueIds") List<Long> issueIds,
                             @Param("lastUpdatedBy") Long lastUpdatedBy,
                             @Param("lastUpdateDate") Date lastUpdateDate);

    List<Long> selectExportIssueIdsInProgram(@Param("programId") Long programId, @Param("searchVO") SearchVO searchVO);

    List<FeatureExportDO> selectExportIssuesInProgram(@Param("programId") Long programId, @Param("issueIds") List<Long> issueIds);

    List<PiExportNameDO> queryPiNameByIssueIds(@Param("programId") Long programId, @Param("issueIds") List<Long> issueIds);

    List<PiExportNameDO> queryActivePiNameByIssueIds(@Param("programId") Long programId, @Param("issueIds") List<Long> issueIds);

    List<Long> querySubBugIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<Long> querySubBugIdsByIssueId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    List<FeatureCommonDTO> selectFeatureByPiId(@Param("programId") Long programId, @Param("piId") Long piId);

    void updateEpicIdOfStoryByFeature(@Param("featureId") Long featureId, @Param("updateEpicId") Long updateEpicId);

    void updateFeatureIdOfStoryByFeature(@Param("featureId") Long featureId, @Param("updateFeatureId") Long updateFeatureId);

    void updateEpicIdOfStoryByFeatureList(@Param("featureIds") List<Long> featureIds, @Param("updateEpicId") Long updateEpicId);

    List<IssueDTO> selectByFeatureIds(@Param("featureIds") List<Long> featureIds);

    List<EpicDataDTO> selectEpicByProgram(@Param("programId") Long programId, @Param("projectId") Long projectId);
}