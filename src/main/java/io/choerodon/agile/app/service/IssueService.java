package io.choerodon.agile.app.service;


import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.infra.dataobject.IssueInfoDO;
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

    /**
     * 创建issue子任务
     *
     * @param issueSubCreateDTO issueSubCreateDTO
     * @return IssueSubDTO
     */
    IssueSubDTO createSubIssue(IssueSubCreateDTO issueSubCreateDTO);

    List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds);

    List<IssueSearchDTO> batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds);

    List<IssueSearchDTO> batchIssueToSprint(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO);

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

    Page<IssueNumDTO> queryIssueByOption(Long projectId, Long issueId, String issueNum, Boolean self, String content, PageRequest pageRequest);

    void exportIssues(Long projectId, SearchDTO searchDTO, HttpServletRequest request, HttpServletResponse response);

    void exportIssue(Long projectId, Long issueId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据issueId复制一个issue
     *
     * @param projectId        projectId
     * @param issueId          issueId
     * @param copyConditionDTO copyConditionDTO
     * @return IssueDTO
     */
    IssueDTO copyIssueByIssueId(Long projectId, Long issueId, CopyConditionDTO copyConditionDTO);

    /**
     * 根据issueId转换为子任务
     *
     * @param projectId             projectId
     * @param issueTransformSubTask issueTransformSubTask
     * @return IssueSubDTO
     */
    IssueSubDTO transformedSubTask(Long projectId, IssueTransformSubTask issueTransformSubTask);

    List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds);
}