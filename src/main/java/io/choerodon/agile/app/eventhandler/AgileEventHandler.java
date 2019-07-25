package io.choerodon.agile.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.event.OrganizationCreateEventPayload;
import io.choerodon.agile.api.vo.event.ProjectEvent;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.infra.enums.ProjectCategory;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(AgileEventHandler.class);
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private IssueLinkTypeService issueLinkTypeService;
    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;
    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;
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

    @SagaTask(code = TASK_ORG_CREATE,
            description = "创建组织事件",
            sagaCode = ORG_CREATE,
            seq = 1)
    public String handleOrgaizationCreateByConsumeSagaTask(String data) {
        LOGGER.info("消费创建组织消息{}", data);
        OrganizationCreateEventPayload organizationEventPayload = JSONObject.parseObject(data, OrganizationCreateEventPayload.class);
        Long organizationId = organizationEventPayload.getId();
        //初始化时区
        this.handleOrganizationInitTimeZoneSagaTask(organizationEventPayload);
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

    private void handleOrganizationInitTimeZoneSagaTask(OrganizationCreateEventPayload organizationEventPayload) {
        Long organizationId = organizationEventPayload.getId();
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
    }
}
