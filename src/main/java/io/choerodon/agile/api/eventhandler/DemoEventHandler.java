package io.choerodon.agile.api.eventhandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.app.service.DemoService;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.domain.agile.event.DemoProjectPayload;
import io.choerodon.agile.domain.agile.event.OrganizationCreateEventPayload;
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

    private static final String DEMO_AGILE_ORG_CREATE_EVENT = "demo-agile-org-create-event";
    private static final String REGISTER_AGILE_INIT_ORG = "register-agile-init-org";

    private static final String DEMO_AGILE_PRO_CREATE_EVENT = "demo-agile-pro-create-event";
    private static final String REGISTER_AGILE_INIT_PROJECT = "register-agile-init-project";

    private static final String DEMO_AGILE_PRO_DEMO_INIT = "demo-agile-pro-demo-init";
    private static final String REGISTER_AGILE_INIT_DEMO_DATA = "register-agile-init-demo-data";

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

    private void demoHandleOrgInitTimeZoneSagaTask(String data) {
        OrganizationRegisterEventPayload organizationRegisterEventPayload = JSONObject.parseObject(data, OrganizationRegisterEventPayload.class);
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
        LOGGER.info("demo接受组织创建消息{}", data);
    }

    @SagaTask(code = DEMO_AGILE_ORG_CREATE_EVENT,
            description = "接收org服务创建组织事件初始化时区",
            sagaCode = REGISTER_AGILE_INIT_ORG,
            seq = 60)
    public String orgCreateForDemoInitTimezone(String message) {
        demoHandleOrgInitTimeZoneSagaTask(message);
        return message;
    }

    @SagaTask(code = DEMO_AGILE_PRO_CREATE_EVENT,
            description = "demo消费创建项目事件初始化项目数据",
            sagaCode = REGISTER_AGILE_INIT_PROJECT,
            seq = 100)
    public String demoInitProject(String message) {
        OrganizationRegisterEventPayload organizationRegisterEventPayload = JSONObject.parseObject(message, OrganizationRegisterEventPayload.class);
        ProjectEvent projectEvent = new ProjectEvent();
        projectEvent.setProjectId(organizationRegisterEventPayload.getProject().getId());
        projectEvent.setProjectCode(organizationRegisterEventPayload.getProject().getCode());
        projectEvent.setProjectName(organizationRegisterEventPayload.getProject().getName());
        projectInfoService.initializationProjectInfo(projectEvent);
        issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
        LOGGER.info("demo接受项目创建消息{}", message);
        return message;
    }

    /**
     * demo项目创建消费
     * @param message
     * @return
     */
    @SagaTask(code = DEMO_AGILE_PRO_DEMO_INIT,
              description = "demo项目创建消费初始化数据",
              sagaCode = REGISTER_AGILE_INIT_DEMO_DATA,
              seq = 170)
    public String demoForAgileInit(String message) {
        OrganizationRegisterEventPayload demoProjectPayload = JSONObject.parseObject(message, OrganizationRegisterEventPayload.class);
        OrganizationRegisterEventPayload result = demoService.demoInit(demoProjectPayload);
        return JSON.toJSONString(result);
    }
}
