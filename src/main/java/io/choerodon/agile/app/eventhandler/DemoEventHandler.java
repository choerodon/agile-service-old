package io.choerodon.agile.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.app.service.DemoService;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.app.service.TimeZoneWorkCalendarService;
import io.choerodon.agile.api.vo.event.OrganizationRegisterEventPayload;
import io.choerodon.agile.api.vo.event.OrganizationRegisterPayload;
import io.choerodon.agile.api.vo.event.ProjectEvent;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/27.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DemoEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgileEventHandler.class);

    private static final String REGISTER_AGILE_INIT_ORG = "register-agile-init-org";
    private static final String REGISTER_AGILE_INIT_PROJECT = "register-agile-init-project";
    private static final String REGISTER_AGILE_INIT_DEMO_DATA = "register-agile-init-demo-data";
    private static final String REGISTER_ORG = "register-org";


    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;

//    @Autowired
//    private TimeZoneWorkCalendarRepository timeZoneWorkCalendarRepository;

    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private IssueLinkTypeService issueLinkTypeService;


    @Autowired
    private DemoService demoService;

    private void demoHandleOrgInitTimeZoneSagaTask(OrganizationRegisterPayload organizationRegisterPayload) {
        Long organizationId = organizationRegisterPayload.getOrganization().getId();
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
        timeZoneWorkCalendarDTO.setOrganizationId(organizationId);
        TimeZoneWorkCalendarDTO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDTO);
        if (query == null) {
            TimeZoneWorkCalendarDTO timeZoneWorkCalendar = new TimeZoneWorkCalendarDTO();
            timeZoneWorkCalendar.setOrganizationId(organizationId);
            timeZoneWorkCalendar.setAreaCode("Asia");
            timeZoneWorkCalendar.setTimeZoneCode("Asia/Shanghai");
            timeZoneWorkCalendar.setSaturdayWork(false);
            timeZoneWorkCalendar.setSundayWork(false);
            timeZoneWorkCalendar.setUseHoliday(true);
            timeZoneWorkCalendarService.create(timeZoneWorkCalendar);
        }
    }

    @SagaTask(code = REGISTER_AGILE_INIT_ORG,
            description = "接收org服务创建组织事件初始化时区",
            sagaCode = REGISTER_ORG,
            seq = 60)
    public OrganizationRegisterPayload orgCreateForDemoInitTimezone(String message) {
        LOGGER.info("demo接受组织创建消息{}", message);
        OrganizationRegisterPayload organizationRegisterPayload = JSONObject.parseObject(message, OrganizationRegisterPayload.class);
        demoHandleOrgInitTimeZoneSagaTask(organizationRegisterPayload);
        return organizationRegisterPayload;
    }

    @SagaTask(code = REGISTER_AGILE_INIT_PROJECT,
            description = "demo消费创建项目事件初始化项目数据",
            sagaCode = REGISTER_ORG,
            seq = 100)
    public OrganizationRegisterPayload demoInitProject(String message) {
        OrganizationRegisterPayload organizationRegisterPayload = JSONObject.parseObject(message, OrganizationRegisterPayload.class);
        ProjectEvent projectEvent = new ProjectEvent();
        projectEvent.setProjectId(organizationRegisterPayload.getProject().getId());
        projectEvent.setProjectCode(organizationRegisterPayload.getProject().getCode());
        projectEvent.setProjectName(organizationRegisterPayload.getProject().getName());
        projectInfoService.initializationProjectInfo(projectEvent);
        issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
        LOGGER.info("demo接受项目创建消息{}", message);
        return organizationRegisterPayload;
    }

    /**
     * demo项目创建消费
     *
     * @param message
     * @return
     */
    @SagaTask(code = REGISTER_AGILE_INIT_DEMO_DATA,
            description = "demo项目创建消费初始化数据",
            sagaCode = REGISTER_ORG,
            seq = 170)
    public OrganizationRegisterEventPayload demoForAgileInit(String message) {
        OrganizationRegisterEventPayload demoProjectPayload = JSONObject.parseObject(message, OrganizationRegisterEventPayload.class);
        return demoService.demoInit(demoProjectPayload);
    }
}
