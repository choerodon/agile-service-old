package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.infra.dataobject.IssueComponentDetailDTO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public interface IssueService {

    void setIssueMapper(IssueMapper issueMapper);

    IssueDTO queryIssueCreate(Long projectId, Long issueId);

    void handleInitIssue(IssueE issueE, Long statusId, ProjectInfoE projectInfoE);

    void afterCreateIssue(Long issueId, IssueE issueE, IssueCreateDTO issueCreateDTO, ProjectInfoE projectInfoE);

    void afterCreateSubIssue(Long issueId, IssueE subIssueE, IssueSubCreateDTO issueSubCreateDTO, ProjectInfoE projectInfoE);

    /**
     * 查询单个issue
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueDTO
     */
    IssueDTO queryIssue(Long projectId, Long issueId, Long organizationId);

    /**
     * 分页过滤查询issueList（包含子任务）
     *
     * @param projectId   projectId
     * @param searchDTO   searchDTO
     * @param pageRequest pageRequest
     * @return IssueListDTO
     */
    Page<IssueListDTO> listIssueWithSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    List<EpicDataDTO> listEpic(Long projectId);

    List<StoryMapEpicDTO> listStoryMapEpic(Long projectId, Long organizationId, Boolean showDoneEpic, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds);

    /**
     * 更新issue
     *
     * @param projectId      projectId
     * @param issueUpdateDTO issueUpdateDTO
     * @param fieldList      fieldList
     * @return IssueDTO
     */
    IssueDTO updateIssue(Long projectId, IssueUpdateDTO issueUpdateDTO, List<String> fieldList);

    /**
     * 更新issue的状态
     *
     * @param projectId
     * @param issueId
     * @param transformId
     * @return
     */
    IssueDTO updateIssueStatus(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType);

    /**
     * 更新issue自己的字段
     *
     * @param issueUpdateDTO
     * @param fieldList
     * @param projectId
     */
    void handleUpdateIssue(IssueUpdateDTO issueUpdateDTO, List<String> fieldList, Long projectId);

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

    void handleInitSubIssue(IssueE subIssueE, Long statusId, ProjectInfoE projectInfoE);

    IssueSubDTO queryIssueSubByCreate(Long projectId, Long issueId);

    List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds);

    void batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds);

    void batchToVersionInStoryMap(Long projectId, Long versionId, StoryMapMoveDTO storyMapMoveDTO);

    List<IssueSearchDTO> batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds);

    List<IssueSearchDTO> batchIssueToEpicInStoryMap(Long projectId, Long epicId, StoryMapMoveDTO storyMapMoveDTO);

    List<IssueSearchDTO> batchIssueToSprint(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO);

    List<IssueSearchDTO> batchIssueToSprintInStoryMap(Long projectId, Long sprintId, StoryMapMoveDTO storyMapMoveDTO);

    /**
     * 根据项目id查询epic
     *
     * @param projectId projectId
     * @return IssueEpicDTO
     */
    List<IssueEpicDTO> listEpicSelectData(Long projectId);

    /**
     * 查询单个子任务信息
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueSubDTO
     */
    IssueSubDTO queryIssueSub(Long projectId, Long organizationId, Long issueId);

    /**
     * 更改issue类型
     *
     * @param issueE             issueE
     * @param issueUpdateTypeDTO issueUpdateTypeDTO
     * @return IssueDTO
     */
    IssueDTO updateIssueTypeCode(IssueE issueE, IssueUpdateTypeDTO issueUpdateTypeDTO, Long organizationId);

    /**
     * 通过项目id和issueId查询issueE
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueE
     */
    IssueE queryIssueByProjectIdAndIssueId(Long projectId, Long issueId);

    Page<IssueNumDTO> queryIssueByOption(Long projectId, Long issueId, String issueNum, Boolean onlyActiveSprint, Boolean self, String content, PageRequest pageRequest);

    void exportIssues(Long projectId, SearchDTO searchDTO, HttpServletRequest request, HttpServletResponse response, Long organizationId);

    /**
     * 根据issueId复制一个issue
     *
     * @param projectId        projectId
     * @param issueId          issueId
     * @param copyConditionDTO copyConditionDTO
     * @return IssueDTO
     */
    IssueDTO cloneIssueByIssueId(Long projectId, Long issueId, CopyConditionDTO copyConditionDTO, Long organizationId, String applyType);

    /**
     * 根据issueId转换为子任务
     *
     * @param projectId             projectId
     * @param issueTransformSubTask issueTransformSubTask
     * @return IssueSubDTO
     */
    IssueSubDTO transformedSubTask(Long projectId, Long organizationId, IssueTransformSubTask issueTransformSubTask);

    /**
     * 子任务转换为任务
     *
     * @param issueE
     * @param issueTransformTask
     * @param organizationId
     * @return
     */
    IssueDTO transformedTask(IssueE issueE, IssueTransformTask issueTransformTask, Long organizationId);

    List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds);

    /**
     * 参数查询issueList提供给测试模块
     *
     * @param projectId   projectId
     * @param searchDTO   searchDTO
     * @param pageRequest pageRequest
     * @return IssueListDTO
     */
    Page<IssueListTestDTO> listIssueWithoutSubToTestComponent(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    Page<IssueListTestDTO> listIssueWithLinkedIssues(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    List<IssueCreationNumDTO> queryIssueNumByTimeSlot(Long projectId, String typeCode, Integer timeSlot);

    /**
     * 参数查询issue列表，不对外开放
     *
     * @param projectId   projectId
     * @param issueId     issueId
     * @param issueNum    issueNum
     * @param self        self
     * @param content     content
     * @param pageRequest pageRequest
     * @return IssueNumDTO
     */
    Page<IssueNumDTO> queryIssueByOptionForAgile(Long projectId, Long issueId, String issueNum,
                                                 Boolean self, String content, PageRequest pageRequest);

    /**
     * 拖动epic
     *
     * @param projectId       projectId
     * @param epicSequenceDTO epicSequenceDTO
     * @return EpicDataDTO
     */
    EpicDataDTO dragEpic(Long projectId, EpicSequenceDTO epicSequenceDTO);

    /**
     * 查询issue统计信息
     *
     * @param projectId  projectId
     * @param type       type
     * @param issueTypes issueTypes要排除的issue类型
     * @return PieChartDTO
     */
    List<PieChartDTO> issueStatistic(Long projectId, String type, List<String> issueTypes);

    /**
     * 测试模块查询issue详情列表
     *
     * @param projectId   projectId
     * @param searchDTO   searchDTO
     * @param pageRequest pageRequest
     * @return IssueComponentDetailTO
     */
    Page<IssueComponentDetailDTO> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

    List<StoryMapIssueDTO> listIssuesByProjectId(Long projectId, String type, String pageType, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds);

    IssueDTO issueParentIdUpdate(Long projectId, IssueUpdateParentIdDTO issueUpdateParentIdDTO);

    void storymapMove(Long projectId, StoryMapMoveDTO storyMapMoveDTO);

    JSONObject countUnResolveByProjectId(Long projectId);

    List<Long> queryIssueIdsByOptions(Long projectId, SearchDTO searchDTO);

    List<UndistributedIssueDTO> queryUnDistributedIssues(Long projectId);

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
     * @return IssueProjectDTO
     */
    List<IssueProjectDTO> queryIssueTestGroupByProject();

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
     * @param searchDTO searchDTO
     * @param projectId projectId
     */
    Boolean handleSearchUser(SearchDTO searchDTO, Long projectId);

    Boolean checkEpicName(Long projectId, String epicName);

    void dealFeatureAndEpicWhenJoinProgram(Long programId, Long projectId);

}