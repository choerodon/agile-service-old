package io.choerodon.agile.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.app.service.DemoService;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.domain.agile.event.OrganizationRegisterEventPayload;
import io.choerodon.agile.domain.agile.event.ProjectEvent;
import io.choerodon.agile.domain.agile.repository.TimeZoneWorkCalendarRepository;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
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

    @Autowired
    private TimeZoneWorkCalendarRepository timeZoneWorkCalendarRepository;

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private IssueLinkTypeService issueLinkTypeService;


    @Autowired
    private DemoService demoService;

    private void demoHandleOrgInitTimeZoneSagaTask(OrganizationRegisterEventPayload organizationRegisterEventPayload) {
        Long organizationId = organizationRegisterEventPayload.getOrganization().getId();
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO();
        timeZoneWorkCalendarDO.setOrganizationId(organizationId);
        TimeZoneWorkCalendarDO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDO);
        if (query == null) {
            TimeZoneWorkCalendarE timeZoneWorkCalendarE = new TimeZoneWorkCalendarE();
            timeZoneWorkCalendarE.setOrganizationId(organizationId);
            timeZoneWorkCalendarE.setAreaCode("Asia");
            timeZoneWorkCalendarE.setTimeZoneCode("Asia/Shanghai");
            timeZoneWorkCalendarE.setSaturdayWork(false);
            timeZoneWorkCalendarE.setSundayWork(false);
            timeZoneWorkCalendarE.setUseHoliday(true);
            timeZoneWorkCalendarRepository.create(timeZoneWorkCalendarE);
        }
    }

    @SagaTask(code = REGISTER_AGILE_INIT_ORG,
            description = "接收org服务创建组织事件初始化时区",
            sagaCode = REGISTER_ORG,
            seq = 60)
    public OrganizationRegisterEventPayload orgCreateForDemoInitTimezone(String message) {
        LOGGER.info("demo接受组织创建消息{}", message);
        OrganizationRegisterEventPayload organizationRegisterEventPayload = JSONObject.parseObject(message, OrganizationRegisterEventPayload.class);
        demoHandleOrgInitTimeZoneSagaTask(organizationRegisterEventPayload);
        return organizationRegisterEventPayload;
    }

    @SagaTask(code = REGISTER_AGILE_INIT_PROJECT,
            description = "demo消费创建项目事件初始化项目数据",
            sagaCode = REGISTER_ORG,
            seq = 100)
    public OrganizationRegisterEventPayload demoInitProject(String message) {
        OrganizationRegisterEventPayload organizationRegisterEventPayload = JSONObject.parseObject(message, OrganizationRegisterEventPayload.class);
        ProjectEvent projectEvent = new ProjectEvent();
        projectEvent.setProjectId(organizationRegisterEventPayload.getProject().getId());
        projectEvent.setProjectCode(organizationRegisterEventPayload.getProject().getCode());
        projectEvent.setProjectName(organizationRegisterEventPayload.getProject().getName());
        projectInfoService.initializationProjectInfo(projectEvent);
        issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
        LOGGER.info("demo接受项目创建消息{}", message);
        return organizationRegisterEventPayload;
    }

    /**
     * demo项目创建消费
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
