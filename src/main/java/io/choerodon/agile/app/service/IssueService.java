package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.domain.agile.entity.IssueE;
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

    /**
     * 创建issue
     *
     * @param issueCreateDTO issueCreateDTO
     * @return IssueDTO
     */
    IssueDTO createIssue(IssueCreateDTO issueCreateDTO);

    /**
     * 查询单个issue
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueDTO
     */
    IssueDTO queryIssue(Long projectId, Long issueId);

    /**
     * 分页过滤查询issueList（不包含子任务）
     *
     * @param projectId   projectId
     * @param searchDTO   searchDTO
     * @param pageRequest pageRequest
     * @return IssueListDTO
     */
    Page<IssueListDTO> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

    List<EpicDataDTO> listEpic(Long projectId);

    List<StoryMapEpicDTO> listStoryMapEpic(Long projectId, Boolean showDoneEpic, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds);

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
     * 删除issue
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return int
     */
    void deleteIssue(Long projectId, Long issueId);

    void batchDeleteIssues(Long projectId, List<Long> issueIds);

    /**
     * 创建issue子任务
     *
     * @param issueSubCreateDTO issueSubCreateDTO
     * @return IssueSubDTO
     */
    IssueSubDTO createSubIssue(IssueSubCreateDTO issueSubCreateDTO);

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
    IssueSubDTO queryIssueSub(Long projectId, Long issueId);

    /**
     * 更改issue类型
     *
     * @param issueE             issueE
     * @param issueUpdateTypeDTO issueUpdateTypeDTO
     * @return IssueDTO
     */
    IssueDTO updateIssueTypeCode(IssueE issueE, IssueUpdateTypeDTO issueUpdateTypeDTO);

    /**
     * 通过项目id和issueId查询issueE
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueE
     */
    IssueE queryIssueByProjectIdAndIssueId(Long projectId, Long issueId);

    Page<IssueCommonDTO> listByOptions(Long projectId, String typeCode, PageRequest pageRequest);

    Page<IssueNumDTO> queryIssueByOption(Long projectId, Long issueId, String issueNum, Boolean onlyActiveSprint, Boolean self, String content, PageRequest pageRequest);

    void exportIssues(Long projectId, SearchDTO searchDTO, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据issueId复制一个issue
     *
     * @param projectId        projectId
     * @param issueId          issueId
     * @param copyConditionDTO copyConditionDTO
     * @return IssueDTO
     */
    IssueDTO cloneIssueByIssueId(Long projectId, Long issueId, CopyConditionDTO copyConditionDTO);

    /**
     * 根据issueId转换为子任务
     *
     * @param projectId             projectId
     * @param issueTransformSubTask issueTransformSubTask
     * @return IssueSubDTO
     */
    IssueSubDTO transformedSubTask(Long projectId, IssueTransformSubTask issueTransformSubTask);

    List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds);

    /**
     * 参数查询issueList提供给测试模块
     *
     * @param projectId   projectId
     * @param searchDTO   searchDTO
     * @param pageRequest pageRequest
     * @return IssueListDTO
     */
    Page<IssueListDTO> listIssueWithoutSubToTestComponent(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

    Page<IssueListDTO> listIssueWithLinkedIssues(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

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

    List<StoryMapIssueDTO> listIssuesByProjectId(Long projectId, String type, String pageType, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds);

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
}