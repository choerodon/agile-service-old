package io.choerodon.agile.infra.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.choerodon.agile.api.dto.SearchDTO;
import org.apache.ibatis.annotations.Param;

import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.mybatis.common.BaseMapper;


/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public interface IssueMapper extends BaseMapper<IssueDO> {

    int removeFromSprint(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    /**
     * 根据issueId查询issueDetail
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueDetailDO
     */
    IssueDetailDO queryIssueDetail(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    List<EpicDataDO> queryEpicList(@Param("projectId") Long projectId);

    int batchIssueToVersion(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("issueIds") List<Long> issueIds, @Param("date") Date date, @Param("userId") Long userId);

    int batchIssueToEpic(@Param("projectId") Long projectId, @Param("epicId") Long epicId, @Param("issueIds") List<Long> issueIds);

    List<IssueSearchDO> queryIssueByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 根据项目id查询issue中的epic
     *
     * @param projectId projectId
     * @return IssueDO
     */
    List<IssueDO> queryIssueEpicSelectList(@Param("projectId") Long projectId);

    Integer batchRemoveFromVersion(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    Integer batchRemoveFromVersionTest(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    String queryRank(@Param("projectId") Long projectId,  @Param("outsetIssueId") Long outsetIssueId);

    List<Long> queryIssueIdOrderByRankDesc(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<Long> queryIssueIdOrderByRankAsc(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    String queryRightRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("leftRank") String leftRank);

    String queryLeftRank(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("rightRank") String rightRank);

    /**
     * 查询issue子任务列表
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueDO
     */
    List<IssueDO> queryIssueSubList(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

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

    List<IssueCountDO> queryDoneIssueCountByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryNotEstimateIssueCountByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueCountDO> queryTotalEstimateByEpicIds(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<IssueLabelDO> selectLabelNameByIssueId(@Param("issueId") Long issueId);

    List<Long> querySubTaskIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    int issueToDestinationByIds(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId, @Param("issueIds") List<Long> issueIds, @Param("date") Date date, @Param("userId") Long userId);

    int batchUpdateIssueRank(@Param("projectId") Long projectId, @Param("moveIssues") List<MoveIssueDO> moveIssues);

    int batchUpdateMapIssueRank(@Param("projectId") Long projectId, @Param("storyMapMoveIssueDOS") List<StoryMapMoveIssueDO> storyMapMoveIssueDOS);

    List<Long> querySubIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    int removeIssueFromSprintByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<Long> querySubIssueIdsByIssueId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    int deleteIssueFromSprintByIssueId(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

    SprintNameDO queryActiveSprintNameByIssueId(@Param("issueId") Long issueId);

    List<SprintNameDO> querySprintNameByIssueId(@Param("issueId") Long issueId);

    IssueDO queryIssueSprintNotClosed(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

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
     * @return IssueNumDO
     */
    List<IssueNumDO> queryIssueByOptionForAgile(@Param("projectId") Long projectId,
                                                @Param("issueId") Long issueId,
                                                @Param("issueNum") String issueNum,
                                                @Param("self") Boolean self,
                                                @Param("content") String content);

    List<ExportIssuesDO> queryExportIssues(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("projectCode") String projectCode);

    List<SprintNameDO> querySprintNameByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    List<VersionIssueRelDO> queryVersionNameByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds, @Param("relationType") String relationType);

    /**
     * 根据issueIds查询issueEpic信息
     *
     * @param projectId projectId
     * @param issueIds  issueIds
     * @return IssueDO
     */
    List<IssueDO> queryIssueEpicInfoByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    IssueNumDO queryIssueByIssueNumOrIssueId(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("issueNum") String issueNum);

    List<IssueInfoDO> listByIssueIds(@Param("projectId") Long prjectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 根据参数查询issueList提供给测试模块
     *
     * @param projectId          projectId
     * @param searchArgs         searchArgs
     * @param advancedSearchArgs advancedSearchArgs
     * @param otherArgs          otherArgs
     * @param content            content
     * @return IssueDO
     */
    List<IssueDO> listIssueWithoutSubToTestComponent(@Param("projectId") Long projectId,
                                                     @Param("searchArgs") Map<String, Object> searchArgs,
                                                     @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs,
                                                     @Param("otherArgs") Map<String, Object> otherArgs,
                                                     @Param("content") String content);

    List<IssueDO> listIssueWithLinkedIssues(@Param("projectId") Long projectId,
                                            @Param("searchArgs") Map<String, Object> searchArgs,
                                            @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs,
                                            @Param("otherArgs") Map<String, Object> otherArgs,
                                            @Param("content") String content);

    List<IssueCreationNumDO> queryIssueNumByTimeSlot(@Param("projectId") Long projectId,
                                                     @Param("typeCode") String typeCode,
                                                     @Param("date") Date date);

    List<Long> queryInVersionIssueIds(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("issueIds") List<Long> issueIds);

    /**
     * 查询issue和issue没有关闭的冲刺
     *
     * @param issueId issueId
     * @return IssueDO
     */
    IssueDO queryIssueWithNoCloseSprint(@Param("issueId") Long issueId);

    /**
     * 根据id查询epic
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return EpicDataDO
     */
    EpicDataDO queryEpicListByEpic(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

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
     * @param projectId          projectId
     * @param searchArgs         searchArgs
     * @param advancedSearchArgs advancedSearchArgs
     * @param otherArgs          otherArgs
     * @param content            content
     * @return IssueComponentDetailDO
     */
    List<IssueComponentDetailDO> listIssueWithoutSubDetail(@Param("projectId") Long projectId, @Param("searchArgs") Map<String, Object> searchArgs,
                                                           @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs,
                                                           @Param("otherArgs") Map<String, Object> otherArgs, @Param("content") String content);

    /**
     * 待办事项查询相关issue的issueIds
     *
     * @param projectId          projectId
     * @param userId             userId
     * @param advancedSearchArgs advancedSearchArgs
     * @param filterSql          filterSql
     * @return issueIds
     */
    List<Long> querySprintIssueIdsByCondition(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs, @Param("filterSql") String filterSql, @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

    List<StoryMapIssueDO> listIssuesByProjectIdSprint(@Param("projectId") Long projectId,
                                                      @Param("pageType") String pageType,
                                                      @Param("assigneeId") Long assigneeId,
                                                      @Param("onlyStory") Boolean onlyStory,
                                                      @Param("filterSql") String filterSql,
                                                      @Param("doneIds") List<Long> doneIds,
                                                      @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

    List<StoryMapIssueDO> listIssuesByProjectIdVersion(@Param("projectId") Long projectId,
                                                       @Param("pageType") String pageType,
                                                       @Param("assigneeId") Long assigneeId,
                                                       @Param("onlyStory") Boolean onlyStory,
                                                       @Param("filterSql") String filterSql,
                                                       @Param("doneIds") List<Long> doneIds,
                                                       @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

    List<StoryMapIssueDO> listIssuesByProjectIdNone(@Param("projectId") Long projectId,
                                                    @Param("pageType") String pageType,
                                                    @Param("assigneeId") Long assigneeId,
                                                    @Param("onlyStory") Boolean onlyStory,
                                                    @Param("filterSql") String filterSql,
                                                    @Param("doneIds") List<Long> doneIds,
                                                    @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

    List<StoryMapEpicDO> queryStoryMapEpicList(@Param("projectId") Long projectId,
                                               @Param("showDoneEpic") Boolean showDoneEpic,
                                               @Param("assigneeId") Long assigneeId,
                                               @Param("onlyStory") Boolean onlyStory,
                                               @Param("filterSql") String filterSql);

    Integer countUnResolveByProjectId(Long projectId);

    Integer countIssueByProjectId(Long projectId);

    List<Long> queryIssueIdsByOptions(@Param("projectId") Long projectId,
                                      @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs,
                                      @Param("otherArgs") Map<String, Object> otherArgs);

    List<UndistributedIssueDO> queryUnDistributedIssues(Long projectId);

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
     * @return IssueDO
     */
    List<IssueBurnDownReportDO> queryIssueByEpicId(@Param("projectId") Long projectId, @Param("epicId") Long epicId);

    /**
     * 查询版本下的所有issue
     *
     * @param projectId projectId
     * @param versionId versionId
     * @return IssueDO
     */
    List<IssueBurnDownReportDO> queryIssueByVersionId(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    /**
     * 根据ids查询所有issue
     *
     * @param projectId projectId
     * @param issueIds  issueIds
     * @return IssueDO
     */
    List<IssueDetailDO> queryByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    String selectMinRankByProjectId(@Param("projectId") Long projectId);

    String selectMaxRankByProjectId(@Param("projectId") Long projectId);

    String selectMapRankByIssueId(@Param("projectId") Long projectId, @Param("outsetIssueId") Long outsetIssueId);

    String selectLeftMaxMapRank(@Param("projectId") Long projectId, @Param("currentMapRank") String currentMapRank);

    String selectRightMinMapRank(@Param("projectId") Long projectId, @Param("currentMapRank") String currentMapRank);

    List<Long> selectIssueIdsByProjectId(@Param("projectId") Long projectId);

    void updateMapRank(@Param("projectId") Long projectId, @Param("mapMoveIssueDOS") List<StoryMapMoveIssueDO> mapMoveIssueDOS);

    Integer queryIssueIdsIsTest(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);

    /**
     * 查询epic信息
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return IssueDO
     */
    IssueDO queryEpicDetailByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

    /**
     * 查询epic相关信息
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return IssueDO
     */
    IssueDO queryEpicWithStatusByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

    /**
     * 根据项目分组测试类型issue
     *
     * @return IssueProjectDO
     */
    List<IssueProjectDO> queryIssueTestGroupByProject();

    List<IssueDO> selectAllPriority();

    List<IssueDO> selectAllType();

    void batchUpdatePriority(@Param("issueDOList") List<IssueDO> issueDOList);

    void batchUpdateIssueType(@Param("issueDOForTypeList") List<IssueDO> issueDOForTypeList);

    List<Long> queryIssueIdsListWithSub(@Param("projectId") Long projectId,
                                        @Param("searchDTO") SearchDTO searchDTO,
                                        @Param("filterSql") String filterSql,
                                        @Param("assigneeFilterIds") List<Long> assigneeFilterIds);

    List<IssueDO> queryIssueListWithSubByIssueIds(@Param("issueIds") List<Long> issueIds);

    /**
     * 查询issueIds对应的issueDo
     *
     * @param issueIds issueIds
     * @return IssueDO
     */
    List<IssueDO> queryIssueByIssueIdsAndSubIssueIds(@Param("issueIds") List<Long> issueIds);

    /**
     * 查询issueIds对应的带当前冲刺的issueDO列表
     *
     * @param projectId projectId
     * @param issueIds  issueIds
     * @return IssueDO
     */
    List<IssueDO> queryIssueSprintNotClosedByIssueIds(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);
}