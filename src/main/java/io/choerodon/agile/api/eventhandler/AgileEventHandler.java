package io.choerodon.agile.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.app.service.BoardService;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.domain.agile.event.OrganizationCreateEventPayload;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AgileEventHandler.class);

    private static final String AGILE_INIT_TIMEZONE = "agile-init-timezone";
    private static final String AGILE_INIT_PROJECT = "agile-init-project";
    private static final String IAM_CREATE_PROJECT = "iam-create-project";
    private static final String ORG_CREATE = "org-create-organization";

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
        boardService.initBoard(projectEvent.getProjectId(), projectEvent.getProjectName() + BOARD);
        projectInfoService.initializationProjectInfo(projectEvent);
        issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
        LOGGER.info("接受项目创建消息{}", message);
        return message;
    }

    @SagaTask(code = AGILE_INIT_TIMEZONE, sagaCode = ORG_CREATE, seq = 1, description = "接收org服务创建组织事件初始化时区")
    public String handleOrganizationInitTimeZoneSagaTask(String message) {
        OrganizationCreateEventPayload organizationCreateEventPayload = JSONObject.parseObject(message, OrganizationCreateEventPayload.class);
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
        LOGGER.info("接受组织创建消息{}", message);
        return message;
    }

}
