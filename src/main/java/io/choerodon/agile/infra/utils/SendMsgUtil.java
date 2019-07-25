package io.choerodon.agile.infra.utils;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.NoticeService;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.enums.SchemeApplyType;
import io.choerodon.agile.infra.feign.IamFeignClient;
import io.choerodon.agile.infra.mapper.IssueStatusMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private UserService userService;

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    @Autowired
    private IamFeignClient iamFeignClient;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private SprintMapper sprintMapper;


    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }


    private String convertProjectName(ProjectVO projectVO) {
        String projectName = projectVO.getName();
        String result = projectName.replaceAll(" ", "%20");
        return result;
    }

    @Async
    public void sendMsgByIssueCreate(Long projectId, IssueVO result) {
        //发送消息
        if (SchemeApplyType.AGILE.equals(result.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_created", result);
            String summary = result.getIssueNum() + "-" + result.getSummary();
            String userName = result.getReporterName();
            ProjectVO projectVO = userService.queryProject(projectId);
            if (projectVO == null) {
                throw new CommonException(ERROR_PROJECT_NOTEXIST);
            }
            String projectName = convertProjectName(projectVO);
            String url = URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectVO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getIssueId() + URL_TEMPLATE5 + result.getIssueId();
            siteMsgUtil.issueCreate(userIds, userName, summary, url, result.getReporterId(), projectId);
            if (result.getAssigneeId() != null) {
                List<Long> assigneeIds = new ArrayList<>();
                assigneeIds.add(result.getAssigneeId());
                siteMsgUtil.issueAssignee(assigneeIds, result.getAssigneeName(), summary, url, result.getAssigneeId(), projectId);
            }
        }
    }

    @Async
    public void sendMsgBySubIssueCreate(Long projectId, IssueSubVO result) {
        // 发送消息
        if (SchemeApplyType.AGILE.equals(result.getApplyType())) {
            IssueVO issueVO = new IssueVO();
            issueVO.setReporterId(result.getReporterId());
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_created", issueVO);
            String summary = result.getIssueNum() + "-" + result.getSummary();
            String userName = result.getReporterName();
            ProjectVO projectVO = userService.queryProject(projectId);
            if (projectVO == null) {
                throw new CommonException(ERROR_PROJECT_NOTEXIST);
            }
            String projectName = convertProjectName(projectVO);
            String url = URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectVO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getParentIssueId() + URL_TEMPLATE5 + result.getIssueId();
            siteMsgUtil.issueCreate(userIds, userName, summary, url, result.getReporterId(), projectId);
            if (result.getAssigneeId() != null) {
                List<Long> assigneeIds = new ArrayList<>();
                assigneeIds.add(result.getAssigneeId());
                siteMsgUtil.issueAssignee(assigneeIds, result.getAssigneeName(), summary, url, result.getAssigneeId(), projectId);
            }
        }
    }

    @Async
    public void sendMsgByIssueAssignee(Long projectId, List<String> fieldList, IssueVO result) {
        if (fieldList.contains("assigneeId") && result.getAssigneeId() != null && SchemeApplyType.AGILE.equals(result.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_assigneed", result);
            String summary = result.getIssueNum() + "-" + result.getSummary();
            String userName = result.getAssigneeName();
            ProjectVO projectVO = userService.queryProject(projectId);
            if (projectVO == null) {
                throw new CommonException(ERROR_PROJECT_NOTEXIST);
            }
            String projectName = convertProjectName(projectVO);
            StringBuilder url = new StringBuilder();
            if (SUB_TASK.equals(result.getTypeCode())) {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectVO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getParentIssueId() + URL_TEMPLATE5 + result.getIssueId());
            } else {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectVO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getIssueId() + URL_TEMPLATE5 + result.getIssueId());
            }
            siteMsgUtil.issueAssignee(userIds, userName, summary, url.toString(), result.getAssigneeId(), projectId);
        }
    }

    @Async
    public void sendMsgByIssueComplete(Long projectId, List<String> fieldList, IssueVO result) {
        Boolean completed = issueStatusMapper.selectByStatusId(projectId, result.getStatusId()).getCompleted();
        if (fieldList.contains(STATUS_ID) && completed != null && completed && result.getAssigneeId() != null && SchemeApplyType.AGILE.equals(result.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_solved", result);
            ProjectVO projectVO = userService.queryProject(projectId);
            if (projectVO == null) {
                throw new CommonException(ERROR_PROJECT_NOTEXIST);
            }
            String projectName = convertProjectName(projectVO);
            StringBuilder url = new StringBuilder();
            if (SUB_TASK.equals(result.getTypeCode())) {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectVO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getParentIssueId() + URL_TEMPLATE5 + result.getIssueId());
            } else {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectVO.getOrganizationId() + URL_TEMPLATE3 + result.getIssueNum() + URL_TEMPLATE4 + result.getIssueId() + URL_TEMPLATE5 + result.getIssueId());
            }
            Long[] ids = new Long[1];
            ids[0] = result.getAssigneeId();
            List<UserDTO> userDTOList = iamFeignClient.listUsersByIds(ids, false).getBody();
            String userName = !userDTOList.isEmpty() && userDTOList.get(0) != null ? userDTOList.get(0).getLoginName() + userDTOList.get(0).getRealName() : "";
            String summary = result.getIssueNum() + "-" + result.getSummary();
            siteMsgUtil.issueSolve(userIds, userName, summary, url.toString(), result.getAssigneeId(), projectId);
        }
    }

    @Async
    public void sendMsgByIssueMoveComplete(Long projectId, IssueMoveVO issueMoveVO, IssueDTO issueDTO) {
        // 发送消息
        Boolean completed = issueStatusMapper.selectByStatusId(projectId, issueMoveVO.getStatusId()).getCompleted();
        if (completed != null && completed && issueDTO.getAssigneeId() != null && SchemeApplyType.AGILE.equals(issueDTO.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_solved", modelMapper.map(issueDTO, IssueVO.class));
            ProjectVO projectVO = userService.queryProject(projectId);
            if (projectVO == null) {
                throw new CommonException("error.project.notExist");
            }
            StringBuilder url = new StringBuilder();
            String projectName = convertProjectName(projectVO);
            ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
            projectInfoDTO.setProjectId(projectId);
            List<ProjectInfoDTO> pioList = projectInfoMapper.select(projectInfoDTO);
            ProjectInfoDTO pio = null;
            if (pioList != null && !pioList.isEmpty()) {
                pio = pioList.get(0);
            }
            String pioCode = (pio == null ? "" : pio.getProjectCode());
            if ("sub_task".equals(issueDTO.getTypeCode())) {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectVO.getOrganizationId() + URL_TEMPLATE3 + pioCode + "-" + issueDTO.getIssueNum() + URL_TEMPLATE4 + issueDTO.getParentIssueId() + URL_TEMPLATE5 + issueDTO.getIssueId());
            } else {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectVO.getOrganizationId() + URL_TEMPLATE3 + pioCode + "-" + issueDTO.getIssueNum() + URL_TEMPLATE4 + issueDTO.getIssueId() + URL_TEMPLATE5 + issueDTO.getIssueId());
            }
            String summary = pioCode + "-" + issueDTO.getIssueNum() + "-" + issueDTO.getSummary();
            Long[] ids = new Long[1];
            ids[0] = issueDTO.getAssigneeId();
            List<UserDTO> userDTOList = userService.listUsersByIds(ids);
            String userName = !userDTOList.isEmpty() && userDTOList.get(0) != null ? userDTOList.get(0).getLoginName() + userDTOList.get(0).getRealName() : "";
            siteMsgUtil.issueSolve(userIds, userName, summary, url.toString(), issueDTO.getAssigneeId(), projectId);
        }
    }

    private void getProjectOwnerByProjects(List<Long> projectIds, List<Long> result) {
        RoleAssignmentSearchVO roleAssignmentSearchVO = new RoleAssignmentSearchVO();
        for (Long projectId : projectIds) {
            Long roleId = null;
            List<RoleVO> roleVOS = userService.listRolesWithUserCountOnProjectLevel(projectId, roleAssignmentSearchVO);
            for (RoleVO roleVO : roleVOS) {
                if ("role/project/default/project-owner".equals(roleVO.getCode())) {
                    roleId = roleVO.getId();
                    break;
                }
            }
            if (roleId != null) {
                PageInfo<UserVO> userDTOS = userService.pagingQueryUsersByRoleIdOnProjectLevel(0, 300, roleId, projectId, roleAssignmentSearchVO);
                for (UserVO userVO : userDTOS.getList()) {
                    if (!result.contains(userVO.getId())) {
                        result.add(userVO.getId());
                    }
                }
            }
        }
    }

    @Async
    public void sendPmAndEmailAfterPiComplete(Long programId, PiDTO piDTO) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<ProjectRelationshipVO> projectRelationshipVOList = iamFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        List<Long> projectIds = (projectRelationshipVOList != null && !projectRelationshipVOList.isEmpty() ? projectRelationshipVOList.stream().map(ProjectRelationshipVO::getProjectId).collect(Collectors.toList()) : null);
        if (projectIds == null) {
            return;
        }
        ProjectVO projectVO = userService.queryProject(programId);
        if (projectVO == null) {
            throw new CommonException(ERROR_PROJECT_NOTEXIST);
        }
        List<Long> result = new ArrayList<>();
        getProjectOwnerByProjects(projectIds, result);
        List<SprintDTO> sprintDTOList = sprintMapper.selectListByPiId(programId, piDTO.getId());
        Map<String, Object> params = new HashMap<>();
        params.put("programName", projectVO.getName());
        params.put("piName", piDTO.getCode() + "-" + piDTO.getName());
        params.put("sprintNameList", sprintDTOList != null && !sprintDTOList.isEmpty() ? sprintDTOList.stream().map(SprintDTO::getSprintName).collect(Collectors.joining(",")) : "");
        siteMsgUtil.piComplete(result, customUserDetails.getUserId(), programId, params);
    }
}
