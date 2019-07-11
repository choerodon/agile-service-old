package io.choerodon.agile.app.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.infra.dataobject.IssueComponentDetailDTO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public interface IssueService {

    void setIssueMapper(IssueMapper issueMapper);

    IssueVO queryIssueCreate(Long projectId, Long issueId);

    void handleInitIssue(IssueConvertDTO issueConvertDTO, Long statusId, ProjectInfoE projectInfoE);

    void afterCreateIssue(Long issueId, IssueConvertDTO issueConvertDTO, IssueCreateVO issueCreateVO, ProjectInfoE projectInfoE);

    void afterCreateSubIssue(Long issueId, IssueConvertDTO subIssueConvertDTO, IssueSubCreateVO issueSubCreateVO, ProjectInfoE projectInfoE);

    /**
     * 查询单个issue
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueVO
     */
    IssueVO queryIssue(Long projectId, Long issueId, Long organizationId);

    /**
     * 分页过滤查询issueList（包含子任务）
     *
     * @param projectId   projectId
     * @param searchVO   searchVO
     * @param pageRequest pageRequest
     * @return IssueListVO
     */
    PageInfo<IssueListFieldKVVO> listIssueWithSub(Long projectId, SearchVO searchVO, PageRequest pageRequest, Long organizationId);

    List<EpicDataVO> listEpic(Long projectId);

    List<EpicDataVO> listProgramEpic(Long programId);

//    List<StoryMapEpicDTO> listStoryMapEpic(Long projectId, Long organizationId, Boolean showDoneEpic, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds);

    /**
     * 更新issue
     *
     * @param projectId      projectId
     * @param issueUpdateVO issueUpdateVO
     * @param fieldList      fieldList
     * @return IssueVO
     */
    IssueVO updateIssue(Long projectId, IssueUpdateVO issueUpdateVO, List<String> fieldList);

    /**
     * 更新issue的状态
     *
     * @param projectId
     * @param issueId
     * @param transformId
     * @return
     */
    IssueVO updateIssueStatus(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType);

    /**
     * 更新issue自己的字段
     *
     * @param issueUpdateVO
     * @param fieldList
     * @param projectId
     */
    void handleUpdateIssue(IssueUpdateVO issueUpdateVO, List<String> fieldList, Long projectId);

    /**
     * 删除issue
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return int
     */
    void deleteIssue(Long projectId, Long issueId);

    void batchDeleteIssues(Long projectId, List<Long> issueIds);

    void batchDeleteIssuesAgile(Long projectId, List<Long> issueIds);

    void handleInitSubIssue(IssueConvertDTO subIssueConvertDTO, Long statusId, ProjectInfoE projectInfoE);

    IssueSubVO queryIssueSubByCreate(Long projectId, Long issueId);

    List<IssueSearchVO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds);

    void batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds);

//    void batchToVersionInStoryMap(Long projectId, Long versionId, StoryMapMoveDTO storyMapMoveDTO);

    List<IssueSearchVO> batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds);

    void batchStoryToFeature(Long projectId, Long featureId, List<Long> issueIds);

//    List<IssueSearchVO> batchIssueToEpicInStoryMap(Long projectId, Long epicId, StoryMapMoveDTO storyMapMoveDTO);

    List<IssueSearchVO> batchIssueToSprint(Long projectId, Long sprintId, MoveIssueVO moveIssueVO);

//    List<IssueSearchVO> batchIssueToSprintInStoryMap(Long projectId, Long sprintId, StoryMapMoveDTO storyMapMoveDTO);

    /**
     * 根据项目id查询epic
     *
     * @param projectId projectId
     * @return IssueEpicVO
     */
    List<IssueEpicVO> listEpicSelectData(Long projectId);

    List<IssueFeatureVO> listFeatureSelectData(Long projectId, Long organizationId, Long epicId);

    List<IssueFeatureVO> listFeature(Long projectId, Long organizationId);

    List<IssueEpicVO> listEpicSelectProgramData(Long programId);

    /**
     * 查询单个子任务信息
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueSubVO
     */
    IssueSubVO queryIssueSub(Long projectId, Long organizationId, Long issueId);

    /**
     * 更改issue类型
     *
     * @param issueConvertDTO             issueConvertDTO
     * @param issueUpdateTypeDTO issueUpdateTypeDTO
     * @return IssueVO
     */
    IssueVO updateIssueTypeCode(IssueConvertDTO issueConvertDTO, IssueUpdateTypeDTO issueUpdateTypeDTO, Long organizationId);

    /**
     * 通过项目id和issueId查询issueE
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueConvertDTO
     */
    IssueConvertDTO queryIssueByProjectIdAndIssueId(Long projectId, Long issueId);

    PageInfo<IssueNumVO> queryIssueByOption(Long projectId, Long issueId, String issueNum, Boolean onlyActiveSprint, Boolean self, String content, PageRequest pageRequest);

    void exportIssues(Long projectId, SearchVO searchVO, HttpServletRequest request, HttpServletResponse response, Long organizationId);

    void exportProgramIssues(Long projectId, SearchVO searchVO, HttpServletRequest request, HttpServletResponse response, Long organizationId);

    /**
     * 根据issueId复制一个issue
     *
     * @param projectId        projectId
     * @param issueId          issueId
     * @param copyConditionVO copyConditionVO
     * @return IssueVO
     */
    IssueVO cloneIssueByIssueId(Long projectId, Long issueId, CopyConditionVO copyConditionVO, Long organizationId, String applyType);

    /**
     * 根据issueId转换为子任务
     *
     * @param projectId             projectId
     * @param issueTransformSubTask issueTransformSubTask
     * @return IssueSubVO
     */
    IssueSubVO transformedSubTask(Long projectId, Long organizationId, IssueTransformSubTask issueTransformSubTask);

    /**
     * 子任务转换为任务
     *
     * @param issueConvertDTO
     * @param issueTransformTask
     * @param organizationId
     * @return
     */
    IssueVO transformedTask(IssueConvertDTO issueConvertDTO, IssueTransformTask issueTransformTask, Long organizationId);

    List<IssueInfoVO> listByIssueIds(Long projectId, List<Long> issueIds);

    /**
     * 参数查询issueList提供给测试模块
     *
     * @param projectId   projectId
     * @param searchVO   searchVO
     * @param pageRequest pageRequest
     * @return IssueListVO
     */
    PageInfo<IssueListTestVO> listIssueWithoutSubToTestComponent(Long projectId, SearchVO searchVO, PageRequest pageRequest, Long organizationId);

    PageInfo<IssueListTestWithSprintVersionVO> listIssueWithLinkedIssues(Long projectId, SearchVO searchVO, PageRequest pageRequest, Long organizationId);

    List<IssueCreationNumVO> queryIssueNumByTimeSlot(Long projectId, String typeCode, Integer timeSlot);

    /**
     * 参数查询issue列表，不对外开放
     *
     * @param projectId   projectId
     * @param issueId     issueId
     * @param issueNum    issueNum
     * @param self        self
     * @param content     content
     * @param pageRequest pageRequest
     * @return IssueNumVO
     */
    PageInfo<IssueNumVO> queryIssueByOptionForAgile(Long projectId, Long issueId, String issueNum,
                                                    Boolean self, String content, PageRequest pageRequest);

    /**
     * 拖动epic
     *
     * @param projectId       projectId
     * @param epicSequenceVO epicSequenceVO
     * @return EpicDataVO
     */
    EpicDataVO dragEpic(Long projectId, EpicSequenceVO epicSequenceVO);

    /**
     * 查询issue统计信息
     *
     * @param projectId  projectId
     * @param type       type
     * @param issueTypes issueTypes要排除的issue类型
     * @return PieChartVO
     */
    List<PieChartVO> issueStatistic(Long projectId, String type, List<String> issueTypes);

    /**
     * 测试模块查询issue详情列表
     *
     * @param projectId   projectId
     * @param searchVO   searchVO
     * @param pageRequest pageRequest
     * @return IssueComponentDetailTO
     */
    PageInfo<IssueComponentDetailDTO> listIssueWithoutSubDetail(Long projectId, SearchVO searchVO, PageRequest pageRequest);

//    List<StoryMapIssueDTO> listIssuesByProjectId(Long projectId, String type, String pageType, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds);

    IssueVO issueParentIdUpdate(Long projectId, IssueUpdateParentIdVO issueUpdateParentIdVO);

//    void storymapMove(Long projectId, StoryMapMoveDTO storyMapMoveDTO);

    JSONObject countUnResolveByProjectId(Long projectId);

    List<Long> queryIssueIdsByOptions(Long projectId, SearchVO searchVO);

    PageInfo<UndistributedIssueVO> queryUnDistributedIssues(Long projectId, PageRequest pageRequest);

    List<UnfinishedIssueDTO> queryUnfinishedIssues(Long projectId, Long assigneeId);

    /**
     * 查询用户故事地图泳道
     *
     * @param projectId projectId
     * @return String
     */
    String querySwimLaneCode(Long projectId);

    /**
     * 克隆issue同时生成版本
     *
     * @param projectId projectId
     * @param versionId versionId
     * @param issueIds  issueIds
     * @return new issueIds
     */
    List<Long> cloneIssuesByVersionId(Long projectId, Long versionId, List<Long> issueIds);

    void initMapRank(Long projectId);

    /**
     * 根据项目分组测试类型issue
     *
     * @return IssueProjectVO
     */
    List<IssueProjectVO> queryIssueTestGroupByProject();

    /**
     * 批量把issue根据冲刺判断更新为初始状态
     *
     * @param projectId    projectId
     * @param moveIssueIds moveIssueIds
     * @param sprintId     sprintId
     */
    void batchHandleIssueStatus(Long projectId, List<Long> moveIssueIds, Long sprintId);

    /**
     * 处理高级搜索中的用户搜索
     *
     * @param searchVO searchVO
     * @param projectId projectId
     */
    Boolean handleSearchUser(SearchVO searchVO, Long projectId);

    Boolean checkEpicName(Long projectId, String epicName);

    PageInfo<FeatureCommonVO> queryFeatureList(Long programId, Long organizationId, PageRequest pageRequest, SearchVO searchVO);

    List<FeatureCommonVO> queryFeatureListByPiId(Long programId, Long organizationId, Long piId);
}