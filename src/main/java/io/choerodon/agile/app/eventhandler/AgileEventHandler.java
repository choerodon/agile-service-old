package io.choerodon.agile.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.event.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.infra.repository.BoardColumnRepository;
import io.choerodon.agile.infra.repository.IssueRepository;
import io.choerodon.agile.infra.repository.IssueStatusRepository;
import io.choerodon.agile.infra.repository.TimeZoneWorkCalendarRepository;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/22.
 * Email: fuqianghuang01@gmail.com
 */
@Component
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

    private static final String AGILE_INIT_TIMEZONE = "agile-init-timezone";
    private static final String AGILE_INIT_PROJECT = "agile-init-project";
    private static final String STATE_MACHINE_INIT_PROJECT = "state-machine-init-project";
    private static final String IAM_CREATE_PROJECT = "iam-create-project";
    private static final String ORG_CREATE = "org-create-organization";
    private static final String PROJECT_CREATE_STATE_MACHINE = "project-create-state-machine";
    private static final String ISSUE_SERVICE_CONSUME_STATUS = "issue-service-consume-status";
    private static final String AGILE_REMOVE_STATUS = "agile-remove-status";
    private static final String AGILE_CHANGE_STATUS = "agile-change-status";
    private static final String AGILE_CONSUME_DEPLOY_STATE_MACHINE_SCHEME = "agile-consume-deploy-statemachine-scheme";
    private static final String DEPLOY_STATE_MACHINE = "deploy-state-machine";
    private static final String DEPLOY_STATE_MACHINE_SCHEME = "deploy-state-machine-scheme";
    private static final String PROGRAM_CREATE_STATE_MACHINE = "program-create-state-machine";
    private static final String STATE_MACHINE_INIT_PROGRAM = "state-machine-init-program";
    private static final String PROJECT_CATEGORY_AGILE = "AGILE";
    private static final String PROJECT_CATEGORY_PROGRAM = "PROGRAM";

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
        if (PROJECT_CATEGORY_AGILE.equals(projectEvent.getProjectCategory())) {
            projectInfoService.initializationProjectInfo(projectEvent);
            issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
        } else if (PROJECT_CATEGORY_PROGRAM.equals(projectEvent.getProjectCategory())) {
            projectInfoService.initializationProjectInfo(projectEvent);
            issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
        }
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

    @SagaTask(code = AGILE_INIT_TIMEZONE,
            description = "接收org服务创建组织事件初始化时区",
            sagaCode = ORG_CREATE,
            seq = 1)
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

    @SagaTask(code = AGILE_CHANGE_STATUS,
            description = "agile消费发布状态机事件",
            sagaCode = DEPLOY_STATE_MACHINE,
            seq = 1)
    public void handleDeployStateMachineEvent(String message) {
        LOGGER.info("sagaTask agile_change_status message: {}", message);
        DeployStateMachinePayload deployStateMachinePayload = JSONObject.parseObject(message, DeployStateMachinePayload.class);
        List<RemoveStatusWithProject> removeStatusWithProjects = deployStateMachinePayload.getRemoveStatusWithProjects();
        List<AddStatusWithProject> addStatusWithProjects = deployStateMachinePayload.getAddStatusWithProjects();
        //删除项目下的状态及与列的关联
        if (removeStatusWithProjects != null && !removeStatusWithProjects.isEmpty()) {
            boardColumnRepository.batchDeleteColumnAndStatusRel(removeStatusWithProjects);
        }
        //增加项目下的状态
        if (addStatusWithProjects != null && !addStatusWithProjects.isEmpty()) {
            issueStatusService.batchCreateStatusByProjectIds(addStatusWithProjects, deployStateMachinePayload.getUserId());
        }
    }

    @SagaTask(code = AGILE_CONSUME_DEPLOY_STATE_MACHINE_SCHEME,
            description = "agile消费发布状态机方案事件",
            sagaCode = DEPLOY_STATE_MACHINE_SCHEME,
            seq = 1)
    public String handleConsumeStateMachineSchemeDeployEvent(String message) {
        LOGGER.info("sagaTask agile-consume-deploy-statemachine-scheme message: {}", message);
        StateMachineSchemeDeployUpdateIssue deployUpdateIssue = JSONObject.parseObject(message, StateMachineSchemeDeployUpdateIssue.class);
        List<StateMachineSchemeChangeItem> changeItems = deployUpdateIssue.getChangeItems();
        List<ProjectConfig> projectConfigs = deployUpdateIssue.getProjectConfigs();
        List<RemoveStatusWithProject> removeStatusWithProjects = deployUpdateIssue.getRemoveStatusWithProjects();
        List<AddStatusWithProject> addStatusWithProjects = deployUpdateIssue.getAddStatusWithProjects();
        //增加项目下的状态
        if (addStatusWithProjects != null && !addStatusWithProjects.isEmpty()) {
            issueStatusService.batchCreateStatusByProjectIds(addStatusWithProjects, deployUpdateIssue.getUserId());
        }
        //批量更新项目对应的issue状态
        projectConfigs.forEach(projectConfig -> {
            Long projectId = projectConfig.getProjectId();
            String applyType = projectConfig.getApplyType();
            changeItems.forEach(changeItem -> {
                Long issueTypeId = changeItem.getIssueTypeId();
                List<StateMachineSchemeStatusChangeItem> statusChangeItems = changeItem.getStatusChangeItems();
                statusChangeItems.forEach(statusChangeItem -> {
                    Long oldStatusId = statusChangeItem.getOldStatus().getId();
                    Long newStatusId = statusChangeItem.getNewStatus().getId();
                    issueRepository.updateIssueStatusByIssueTypeId(projectId, applyType, issueTypeId, oldStatusId, newStatusId, deployUpdateIssue.getUserId());
                });
            });
        });
        //删除项目下的状态及与列的关联
        if (removeStatusWithProjects != null && !removeStatusWithProjects.isEmpty()) {
            boardColumnRepository.batchDeleteColumnAndStatusRel(deployUpdateIssue.getRemoveStatusWithProjects());
        }
        issueFeignClient.updateDeployProgress(deployUpdateIssue.getOrganizationId(), deployUpdateIssue.getSchemeId(), 100);
        return message;
    }

    @SagaTask(code = STATE_MACHINE_INIT_PROGRAM,
            description = "状态机服务初始化状态后，消费项目群",
            sagaCode = PROGRAM_CREATE_STATE_MACHINE,
            seq = 3)
    public String dealStateMachineInitProgram(String message) {
        ProjectCreateAgilePayload projectCreateAgilePayload = JSONObject.parseObject(message, ProjectCreateAgilePayload.class);
        ProjectEvent projectEvent = projectCreateAgilePayload.getProjectEvent();
        List<StatusPayload> statusPayloads = projectCreateAgilePayload.getStatusPayloads();
        boardService.initBoardByProgram(projectEvent.getProjectId(), projectEvent.getProjectName() + BOARD, statusPayloads);
        LOGGER.info("项目群创建接受接收状态服务消息{}", message);
        return message;
    }

}
