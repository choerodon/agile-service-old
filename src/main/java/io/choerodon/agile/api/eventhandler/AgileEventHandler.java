package io.choerodon.agile.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.IssueStatusDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.domain.agile.event.*;
import io.choerodon.agile.domain.agile.repository.BoardColumnRepository;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.domain.agile.repository.IssueStatusRepository;
import io.choerodon.agile.domain.agile.repository.TimeZoneWorkCalendarRepository;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.oauth.DetailsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/22.
 * Email: fuqianghuang01@gmail.com
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class AgileEventHandler {

    private static final String BOARD = "-board";

    @Autowired
    private BoardService boardService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private IssueLinkTypeService issueLinkTypeService;
    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;
    @Autowired
    private TimeZoneWorkCalendarRepository timeZoneWorkCalendarRepository;
    @Autowired
    private IssueStatusService issueStatusService;
    @Autowired
    private BoardColumnRepository boardColumnRepository;
    @Autowired
    private IssueStatusRepository issueStatusRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private IssueFeignClient issueFeignClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(AgileEventHandler.class);

    private static final String AGILE_ADD_STATUS = "agile_add_status";
    private static final String AGILE_INIT_TIMEZONE = "agile-init-timezone";
    private static final String AGILE_INIT_PROJECT = "agile-init-project";
    private static final String AGILE_CONSUME_DEPLOY_STATE_MACHINE_SCHEME = "agile-consume-deploy-statemachine-shceme";
    private static final String STATE_MACHINE_INIT_PROJECT = "state-machine-init-project";
    private static final String STATUS_CREATE_CONSUME_ORG = "status-create-consume-org";
    private static final String STATUS_DELETE_CONSUME_ORG = "status-delete-consume-org";
    private static final String IAM_CREATE_PROJECT = "iam-create-project";
    private static final String DEPLOY_STATE_MACHINE_SCHEME = "issue-deploy-statemachine-scheme";
    private static final String ORG_CREATE = "org-create-organization";
    private static final String PROJECT_CREATE_STATE_MACHINE = "project-create-state-machine";
    private static final String ORG_REGISTER = "org-register";
    private static final String DEPLOY_STATEMACHINE_ADD_STATUS = "deploy-statemachine-add-status";
    private static final String ISSUE_SERVICE_CONSUME_STATUS = "issue-service-consume-status";
    private static final String AGILE_REMOVE_STATUS = "agile-remove-status";
    private static final String DEPLOY_STATEMACHINE_DELETE_STATUS = "deploy-statemachine-delete-status";

    /**
     * 创建项目事件
     *
     * @param message message
     */
    @SagaTask(code = AGILE_INIT_PROJECT,
            description = "agile消费创建项目事件初始化项目数据",
            sagaCode = IAM_CREATE_PROJECT,
            seq = 2)
    public String handleProjectInitByConsumeSagaTask(String message) {
        ProjectEvent projectEvent = JSONObject.parseObject(message, ProjectEvent.class);
//        boardService.initBoard(projectEvent.getProjectId(), projectEvent.getProjectName() + BOARD);
        projectInfoService.initializationProjectInfo(projectEvent);
        issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
        LOGGER.info("接受项目创建消息{}", message);
        return message;
    }


    @SagaTask(code = STATE_MACHINE_INIT_PROJECT,
            description = "状态机服务初始化状态后，敏捷消费事件",
            sagaCode = PROJECT_CREATE_STATE_MACHINE,
            seq = 3)
    public String dealStateMachineInitProject(String message) {
        ProjectCreateAgilePayload projectCreateAgilePayload = JSONObject.parseObject(message, ProjectCreateAgilePayload.class);
        ProjectEvent projectEvent = projectCreateAgilePayload.getProjectEvent();
        List<StatusPayload> statusPayloads = projectCreateAgilePayload.getStatusPayloads();
        boardService.initBoard(projectEvent.getProjectId(), projectEvent.getProjectName() + BOARD, statusPayloads);
        LOGGER.info("接受接收状态服务消息{}", message);
        return message;
    }

    @SagaTask(code = STATUS_CREATE_CONSUME_ORG,
            description = "agile消费发布状态机中增加状态",
            sagaCode = DEPLOY_STATEMACHINE_ADD_STATUS,
            seq = 4)
    public void dealDeployStateMachineAddStatus(String message) {
        DeployStatusPayload deployStatusPayload = JSONObject.parseObject(message, DeployStatusPayload.class);
        Map<String, List<Long>> projectIdsMap = deployStatusPayload.getProjectIdsMap();
        List<StatusPayload> statusPayloads = deployStatusPayload.getStatusPayloads();
        //只取敏捷和测试影响到的项目
        List<Long> agileProjectIds = projectIdsMap.get(SchemeApplyType.AGILE) != null ? projectIdsMap.get(SchemeApplyType.AGILE) : new ArrayList<>();
        List<Long> testProjectIds = projectIdsMap.get(SchemeApplyType.TEST) != null ? projectIdsMap.get(SchemeApplyType.TEST) : new ArrayList<>();
        agileProjectIds.addAll(testProjectIds);
        List<Long> projectIds = agileProjectIds.stream().distinct().collect(Collectors.toList());
        LOGGER.info("sagaTask agile_add_status projectIdsMap: {}", projectIdsMap);
        for (Long projectId : projectIds) {
            for (StatusPayload statusPayload : statusPayloads) {
                IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
                issueStatusDTO.setStatusId(statusPayload.getStatusId());
                issueStatusDTO.setCategoryCode(statusPayload.getType());
                issueStatusDTO.setName(statusPayload.getStatusName());
                issueStatusDTO.setProjectId(projectId);
                issueStatusService.createStatusByStateMachine(projectId, issueStatusDTO);
            }
        }
    }

    @SagaTask(code = STATUS_DELETE_CONSUME_ORG,
            description = "agile消费发布状态机中删除状态",
            sagaCode = DEPLOY_STATEMACHINE_DELETE_STATUS,
            seq = 4)
    public void dealDeployStateMachineDeleteStatus(String message) {
        DeployStatusPayload deployStatusPayload = JSONObject.parseObject(message, DeployStatusPayload.class);
        List<RemoveStatusWithProject> removeStatusWithProjects = deployStatusPayload.getRemoveStatusWithProjects();
        //删除状态及与列的关联恭喜
        LOGGER.info("sagaTask agile_delete_status removeStatusWithProjects: {}", removeStatusWithProjects);
        if (removeStatusWithProjects != null && !removeStatusWithProjects.isEmpty()) {
            boardColumnRepository.batchDeleteColumnAndStatusRel(removeStatusWithProjects);
        }
    }

    @SagaTask(code = AGILE_INIT_TIMEZONE, sagaCode = ORG_CREATE, seq = 1, description = "接收org服务创建组织事件初始化时区")
    public String handleOrgaizationCreateByConsumeSagaTask(String message) {
        handleOrganizationInitTimeZoneSagaTask(message);
        return message;
    }

    @SagaTask(code = ISSUE_SERVICE_CONSUME_STATUS,
            sagaCode = AGILE_REMOVE_STATUS,
            seq = 1,
            description = "消费删除状态消息")
    public void issueServiceConSumeStatus(String message) {
        StatusPayload statusPayload = JSONObject.parseObject(message, StatusPayload.class);
        issueStatusService.consumDeleteStatus(statusPayload);
    }

    @SagaTask(code = AGILE_INIT_TIMEZONE,
            description = "issue消费注册组织初始化数据",
            sagaCode = ORG_REGISTER,
            seq = 1)
    public String handleOrgaizationRegisterByConsumeSagaTask(String data) {
        handleOrganizationInitTimeZoneSagaTask(data);
        return data;
    }

    private void handleOrganizationInitTimeZoneSagaTask(String data) {
        OrganizationCreateEventPayload organizationCreateEventPayload = JSONObject.parseObject(data, OrganizationCreateEventPayload.class);
        Long organizationId = organizationCreateEventPayload.getId();
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO();
        timeZoneWorkCalendarDO.setOrganizationId(organizationId);
        TimeZoneWorkCalendarDO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDO);
        if (query == null) {
            TimeZoneWorkCalendarE timeZoneWorkCalendarE = new TimeZoneWorkCalendarE();
            timeZoneWorkCalendarE.setAreaCode("Asia");
            timeZoneWorkCalendarE.setTimeZoneCode("Asia/Shanghai");
            timeZoneWorkCalendarE.setSaturdayWork(false);
            timeZoneWorkCalendarE.setSundayWork(false);
            timeZoneWorkCalendarE.setUseHoliday(true);
            timeZoneWorkCalendarE.setOrganizationId(organizationId);
            timeZoneWorkCalendarRepository.create(timeZoneWorkCalendarE);
        }
        LOGGER.info("接受组织创建消息{}", data);
    }

    /**
     * 消费发布状态机方案事件
     *
     * @param message message
     */
    @SagaTask(code = AGILE_CONSUME_DEPLOY_STATE_MACHINE_SCHEME,
            description = "agile消费发布状态机方案事件",
            sagaCode = DEPLOY_STATE_MACHINE_SCHEME,
            seq = 1)
    public String handleConsumeStateMachineSchemeDeployEven(String message) {
        LOGGER.info("接受发布状态机方案事件{}", message);
        StateMachineSchemeDeployUpdateIssue deployUpdateIssue = JSONObject.parseObject(message, StateMachineSchemeDeployUpdateIssue.class);
        List<ProjectConfig> projectConfigs = deployUpdateIssue.getProjectConfigs();
        List<StateMachineSchemeChangeItem> changeItems = deployUpdateIssue.getChangeItems();
        List<Long> projectIds = projectConfigs.stream().map(ProjectConfig::getProjectId).collect(Collectors.toList());
        List<StatusMapDTO> addStatus = deployUpdateIssue.getAddStatuses();
        if (addStatus != null && !addStatus.isEmpty() && !projectIds.isEmpty()) {
            issueStatusRepository.batchCreateStatusByProjectIds(addStatus, projectIds, DetailsHelper.getUserDetails().getUserId());
        }
        projectConfigs.forEach(projectConfig -> {
            Long projectId = projectConfig.getProjectId();
            String applyType = projectConfig.getApplyType();
            changeItems.forEach(changeItem -> {
                Long issueTypeId = changeItem.getIssueTypeId();
                List<StateMachineSchemeStatusChangeItem> statusChangeItems = changeItem.getStatusChangeItems();
                statusChangeItems.forEach(statusChangeItem -> {
                    Long oldStatusId = statusChangeItem.getOldStatus().getId();
                    Long newStatusId = statusChangeItem.getNewStatus().getId();
                    issueRepository.updateIssueStatusByIssueTypeId(projectId, applyType, issueTypeId, oldStatusId, newStatusId);
                });
            });
        });
        if (!deployUpdateIssue.getRemoveStatusWithProjects().isEmpty() && deployUpdateIssue.getRemoveStatusWithProjects() != null) {
            boardColumnRepository.batchDeleteColumnAndStatusRel(deployUpdateIssue.getRemoveStatusWithProjects());
        }
        issueFeignClient.updateDeployProgress(deployUpdateIssue.getOrganizationId(), deployUpdateIssue.getSchemeId(), 100);
        return message;
    }

}
