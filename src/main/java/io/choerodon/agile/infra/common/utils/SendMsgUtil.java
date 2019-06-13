package io.choerodon.agile.infra.common.utils;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.NoticeService;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.IssueStatusMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.convertor.ConvertHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/29.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class SendMsgUtil {

    private static final String URL_TEMPLATE1 = "#/agile/issue?type=project&id=";
    private static final String URL_TEMPLATE2 = "&name=";
    private static final String URL_TEMPLATE3 = "&paramName=";
    private static final String URL_TEMPLATE4 = "&paramIssueId=";
    private static final String URL_TEMPLATE5 = "&paramOpenIssueId=";
    private static final String URL_TEMPLATE6 = "&organizationId=";
    private static final String ERROR_PROJECT_NOTEXIST = "error.project.notExist";
    private static final String SUB_TASK = "sub_task";
    private static final String STATUS_ID = "statusId";

    @Autowired
    private SiteMsgUtil siteMsgUtil;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private SprintMapper sprintMapper;


    private String convertProjectName(ProjectDTO projectDTO) {
        String projectName = projectDTO.getName();
        String result = projectName.replaceAll(" ", "%20");
        return result;
    }

    @Async
    public void sendMsgByIssueCreate(Long projectId, IssueDTO result) {
        //发送消息
        if (SchemeApplyType.AGILE.equals(result.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_created", result);
            String summary = result.getIssueNum() + "-" + result.getSummary();
            String userName = result.getReporterName();
            ProjectDTO projectDTO = userRepository.queryProject(projectId);
            if (projectDTO == null) {
                throw new CommonException(ERROR_PROJECT_NOTEXIST);
            }
            String projectName = convertProjectName(projectDTO);
            String url = URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getIssueId() + URL_TEMPLATE5 + result.getIssueId();
            siteMsgUtil.issueCreate(userIds, userName, summary, url, result.getReporterId(), projectId);
            if (result.getAssigneeId() != null) {
                List<Long> assigneeIds = new ArrayList<>();
                assigneeIds.add(result.getAssigneeId());
                siteMsgUtil.issueAssignee(assigneeIds, result.getAssigneeName(), summary, url, result.getAssigneeId(), projectId);
            }
        }
    }

    @Async
    public void sendMsgBySubIssueCreate(Long projectId, IssueSubDTO result) {
        // 发送消息
        if (SchemeApplyType.AGILE.equals(result.getApplyType())) {
            IssueDTO issueDTO = new IssueDTO();
            issueDTO.setReporterId(result.getReporterId());
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_created", issueDTO);
            String summary = result.getIssueNum() + "-" + result.getSummary();
            String userName = result.getReporterName();
            ProjectDTO projectDTO = userRepository.queryProject(projectId);
            if (projectDTO == null) {
                throw new CommonException(ERROR_PROJECT_NOTEXIST);
            }
            String projectName = convertProjectName(projectDTO);
            String url = URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getParentIssueId() + URL_TEMPLATE5 + result.getIssueId();
            siteMsgUtil.issueCreate(userIds, userName, summary, url, result.getReporterId(), projectId);
            if (result.getAssigneeId() != null) {
                List<Long> assigneeIds = new ArrayList<>();
                assigneeIds.add(result.getAssigneeId());
                siteMsgUtil.issueAssignee(assigneeIds, result.getAssigneeName(), summary, url, result.getAssigneeId(), projectId);
            }
        }
    }

    @Async
    public void sendMsgByIssueAssignee(Long projectId, List<String> fieldList, IssueDTO result) {
        if (fieldList.contains("assigneeId") && result.getAssigneeId() != null && SchemeApplyType.AGILE.equals(result.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_assigneed", result);
            String summary = result.getIssueNum() + "-" + result.getSummary();
            String userName = result.getAssigneeName();
            ProjectDTO projectDTO = userRepository.queryProject(projectId);
            if (projectDTO == null) {
                throw new CommonException(ERROR_PROJECT_NOTEXIST);
            }
            String projectName = convertProjectName(projectDTO);
            StringBuilder url = new StringBuilder();
            if (SUB_TASK.equals(result.getTypeCode())) {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getParentIssueId() + URL_TEMPLATE5 + result.getIssueId());
            } else {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getIssueId() + URL_TEMPLATE5 + result.getIssueId());
            }
            siteMsgUtil.issueAssignee(userIds, userName, summary, url.toString(), result.getAssigneeId(), projectId);
        }
    }

    @Async
    public void sendMsgByIssueComplete(Long projectId, List<String> fieldList, IssueDTO result) {
        Boolean completed = issueStatusMapper.selectByStatusId(projectId, result.getStatusId()).getCompleted();
        if (fieldList.contains(STATUS_ID) && completed != null && completed && result.getAssigneeId() != null && SchemeApplyType.AGILE.equals(result.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_solved", result);
            ProjectDTO projectDTO = userRepository.queryProject(projectId);
            if (projectDTO == null) {
                throw new CommonException(ERROR_PROJECT_NOTEXIST);
            }
            String projectName = convertProjectName(projectDTO);
            StringBuilder url = new StringBuilder();
            if (SUB_TASK.equals(result.getTypeCode())) {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getParentIssueId() + URL_TEMPLATE5 + result.getIssueId());
            } else {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getIssueId() + URL_TEMPLATE5 + result.getIssueId());
            }
            Long[] ids = new Long[1];
            ids[0] = result.getAssigneeId();
            List<UserDO> userDOList = userFeignClient.listUsersByIds(ids, false).getBody();
            String userName = !userDOList.isEmpty() && userDOList.get(0) != null ? userDOList.get(0).getLoginName() + userDOList.get(0).getRealName() : "";
            String summary = result.getIssueNum() + "-" + result.getSummary();
            siteMsgUtil.issueSolve(userIds, userName, summary, url.toString(), result.getAssigneeId(), projectId);
        }
    }

    @Async
    public void sendMsgByIssueMoveComplete(Long projectId, IssueE issueE, IssueDO issueDO) {
        // 发送消息
        Boolean completed = issueStatusMapper.selectByStatusId(projectId, issueE.getStatusId()).getCompleted();
        if (completed != null && completed && issueDO.getAssigneeId() != null && SchemeApplyType.AGILE.equals(issueDO.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_solved", ConvertHelper.convert(issueDO, IssueDTO.class));
            ProjectDTO projectDTO = userRepository.queryProject(projectId);
            if (projectDTO == null) {
                throw new CommonException("error.project.notExist");
            }
            StringBuilder url = new StringBuilder();
            String projectName = convertProjectName(projectDTO);
            ProjectInfoDO projectInfoDO = new ProjectInfoDO();
            projectInfoDO.setProjectId(projectId);
            List<ProjectInfoDO> pioList = projectInfoMapper.select(projectInfoDO);
            ProjectInfoDO pio = null;
            if (pioList != null && !pioList.isEmpty()) {
                pio = pioList.get(0);
            }
            String pioCode = (pio == null ? "" : pio.getProjectCode());
            if ("sub_task".equals(issueDO.getTypeCode())) {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + pioCode + "-" + issueDO.getIssueNum() + URL_TEMPLATE4 + issueDO.getParentIssueId() + URL_TEMPLATE5 + issueDO.getIssueId());
            } else {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + pioCode + "-" + issueDO.getIssueNum() + URL_TEMPLATE4 + issueDO.getIssueId() + URL_TEMPLATE5 + issueDO.getIssueId());
            }
            String summary = pioCode + "-" + issueDO.getIssueNum() + "-" + issueDO.getSummary();
            Long[] ids = new Long[1];
            ids[0] = issueDO.getAssigneeId();
            List<UserDO> userDOList = userRepository.listUsersByIds(ids);
            String userName = !userDOList.isEmpty() && userDOList.get(0) != null ? userDOList.get(0).getLoginName() + userDOList.get(0).getRealName() : "";
            siteMsgUtil.issueSolve(userIds, userName, summary, url.toString(), issueDO.getAssigneeId(), projectId);
        }
    }

    private void getProjectOwnerByProjects(List<Long> projectIds, List<Long> result) {
        RoleAssignmentSearchDTO roleAssignmentSearchDTO = new RoleAssignmentSearchDTO();
        for (Long projectId : projectIds) {
            Long roleId = null;
            List<RoleDTO> roleDTOS = userRepository.listRolesWithUserCountOnProjectLevel(projectId, roleAssignmentSearchDTO);
            for (RoleDTO roleDTO : roleDTOS) {
                if ("role/project/default/project-owner".equals(roleDTO.getCode())) {
                    roleId = roleDTO.getId();
                    break;
                }
            }
            if (roleId != null) {
                PageInfo<UserDTO> userDTOS = userRepository.pagingQueryUsersByRoleIdOnProjectLevel(0, 300, roleId, projectId, roleAssignmentSearchDTO);
                for (UserDTO userDTO : userDTOS.getList()) {
                    if (!result.contains(userDTO.getId())) {
                        result.add(userDTO.getId());
                    }
                }
            }
        }
    }

    @Async
    public void sendPmAndEmailAfterPiComplete(Long programId, PiE piE) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<ProjectRelationshipDTO> projectRelationshipDTOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        List<Long> projectIds = (projectRelationshipDTOList != null && !projectRelationshipDTOList.isEmpty() ? projectRelationshipDTOList.stream().map(ProjectRelationshipDTO::getProjectId).collect(Collectors.toList()) : null);
        if (projectIds == null) {
            return;
        }
        ProjectDTO projectDTO = userRepository.queryProject(programId);
        if (projectDTO == null) {
            throw new CommonException(ERROR_PROJECT_NOTEXIST);
        }
        List<Long> result = new ArrayList<>();
        getProjectOwnerByProjects(projectIds, result);
        List<SprintDO> sprintDOList = sprintMapper.selectListByPiId(programId, piE.getId());
        Map<String, Object> params = new HashMap<>();
        params.put("programName", projectDTO.getName());
        params.put("piName", piE.getCode() + "-" + piE.getName());
        params.put("sprintNameList", sprintDOList != null && !sprintDOList.isEmpty() ? sprintDOList.stream().map(SprintDO::getSprintName).collect(Collectors.joining(",")) : "");
        siteMsgUtil.piComplete(result, customUserDetails.getUserId(), programId, params);
    }
}
