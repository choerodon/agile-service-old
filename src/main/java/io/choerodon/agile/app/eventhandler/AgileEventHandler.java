package io.choerodon.agile.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.event.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.dataobject.ProjectConfigDTO;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.infra.enums.ProjectCategory;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static io.choerodon.agile.infra.utils.SagaTopic.Organization.ORG_CREATE;
import static io.choerodon.agile.infra.utils.SagaTopic.Organization.TASK_ORG_CREATE;
import static io.choerodon.agile.infra.utils.SagaTopic.Project.PROJECT_CREATE;
import static io.choerodon.agile.infra.utils.SagaTopic.Project.TASK_PROJECT_CREATE;

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
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;
    @Autowired
    private IssueStatusService issueStatusService;
    @Autowired
    private BoardColumnService boardColumnService;
    @Autowired
    private IssueAccessDataService issueAccessDataService;
    @Autowired
    private IssueFeignClient issueFeignClient;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;
    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private InitService initService;


    private static final Logger LOGGER = LoggerFactory.getLogger(AgileEventHandler.class);

    private static final String STATE_MACHINE_INIT_PROJECT = "state-machine-init-project";
    private static final String PROJECT_CREATE_STATE_MACHINE = "project-create-state-machine";
    private static final String ISSUE_SERVICE_CONSUME_STATUS = "issue-service-consume-status";
    private static final String AGILE_REMOVE_STATUS = "agile-remove-status";
    private static final String AGILE_CHANGE_STATUS = "agile-change-status";
    private static final String AGILE_CONSUME_DEPLOY_STATE_MACHINE_SCHEME = "agile-consume-deploy-statemachine-scheme";
    private static final String DEPLOY_STATE_MACHINE = "deploy-state-machine";
    private static final String DEPLOY_STATE_MACHINE_SCHEME = "deploy-state-machine-scheme";
    private static final String PROGRAM_CREATE_STATE_MACHINE = "program-create-state-machine";
    private static final String STATE_MACHINE_INIT_PROGRAM = "state-machine-init-program";

    @SagaTask(code = TASK_ORG_CREATE,
            description = "创建组织事件",
            sagaCode = ORG_CREATE,
            seq = 1)
    public String handleOrgaizationCreateByConsumeSagaTask(String data) {
        LOGGER.info("消费创建组织消息{}", data);
        OrganizationCreateEventPayload organizationEventPayload = JSONObject.parseObject(data, OrganizationCreateEventPayload.class);
        Long organizationId = organizationEventPayload.getId();
        //初始化时区
        handleOrganizationInitTimeZoneSagaTask(data);
        //注册组织初始化问题类型
        issueTypeService.initIssueTypeByConsumeCreateOrganization(organizationId);
        //注册组织初始化优先级
        priorityService.initProrityByOrganization(Arrays.asList(organizationId));
        //初始化状态
        initService.initStatus(organizationId);
        //初始化默认状态机
        initService.initDefaultStateMachine(organizationId);
        return data;
    }

    /**
     * 创建项目事件
     *
     * @param message message
     */
    @SagaTask(code = TASK_PROJECT_CREATE,
            description = "agile消费创建项目事件初始化项目数据",
            sagaCode = PROJECT_CREATE,
            seq = 2)
    public String handleProjectInitByConsumeSagaTask(String message) {
        ProjectEvent projectEvent = JSONObject.parseObject(message, ProjectEvent.class);
        LOGGER.info("接受创建项目消息{}", message);
        if (ProjectCategory.AGILE.equals(projectEvent.getProjectCategory())) {
            //创建projectInfo
            projectInfoService.initializationProjectInfo(projectEvent);
            //创建项目初始化issueLinkType
            issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
            //创建项目时创建默认状态机方案
            stateMachineSchemeService.initByConsumeCreateProject(projectEvent);
            //创建项目时创建默认问题类型方案
            issueTypeSchemeService.initByConsumeCreateProject(projectEvent.getProjectId(), projectEvent.getProjectCode());
        } else if (ProjectCategory.PROGRAM.equals(projectEvent.getProjectCategory())) {
            //创建projectInfo
            projectInfoService.initializationProjectInfo(projectEvent);
            //创建项目初始化issueLinkType
            issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
            //创建项目群时创建默认状态机方案
            stateMachineSchemeService.initByConsumeCreateProgram(projectEvent);
            //创建项目群时创建默认问题类型方案
            issueTypeSchemeService.initByConsumeCreateProgram(projectEvent.getProjectId(), projectEvent.getProjectCode());
        }
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
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
        timeZoneWorkCalendarDTO.setOrganizationId(organizationId);
        TimeZoneWorkCalendarDTO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDTO);
        if (query == null) {
            TimeZoneWorkCalendarDTO timeZoneWorkCalendar = new TimeZoneWorkCalendarDTO();
            timeZoneWorkCalendar.setAreaCode("Asia");
            timeZoneWorkCalendar.setTimeZoneCode("Asia/Shanghai");
            timeZoneWorkCalendar.setSaturdayWork(false);
            timeZoneWorkCalendar.setSundayWork(false);
            timeZoneWorkCalendar.setUseHoliday(true);
            timeZoneWorkCalendar.setOrganizationId(organizationId);
            timeZoneWorkCalendarService.create(timeZoneWorkCalendar);
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
            boardColumnService.batchDeleteColumnAndStatusRel(removeStatusWithProjects);
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
        List<ProjectConfigDTO> projectConfigs = deployUpdateIssue.getProjectConfigs();
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
                    issueAccessDataService.updateIssueStatusByIssueTypeId(projectId, applyType, issueTypeId, oldStatusId, newStatusId, deployUpdateIssue.getUserId());
                });
            });
        });
        //删除项目下的状态及与列的关联
        if (removeStatusWithProjects != null && !removeStatusWithProjects.isEmpty()) {
            boardColumnService.batchDeleteColumnAndStatusRel(deployUpdateIssue.getRemoveStatusWithProjects());
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
