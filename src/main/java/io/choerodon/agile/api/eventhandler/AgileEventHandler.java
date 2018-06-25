package io.choerodon.agile.api.eventhandler;

import io.choerodon.agile.app.service.BoardService;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.event.ProjectEvent;
import io.choerodon.core.event.EventPayload;
import io.choerodon.event.consumer.annotation.EventListener;
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

    private static final String IAM_SERVICE = "iam-service";
    private static final String BOARD = "-board";

    @Autowired
    private BoardService boardService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private IssueLinkTypeService issueLinkTypeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AgileEventHandler.class);

    private void loggerInfo(Object o) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("data: {}", o);
        }
    }

    /**
     * 创建项目事件
     *
     * @param payload payload
     */
    @EventListener(topic = IAM_SERVICE, businessType = "createProject")
    public void handleProjectCreateEvent(EventPayload<ProjectEvent> payload) {
        ProjectEvent projectEvent = payload.getData();
        loggerInfo(projectEvent);
        boardService.initBoard(projectEvent.getProjectId(), projectEvent.getProjectName() + BOARD);
        projectInfoService.initializationProjectInfo(projectEvent);
        issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
    }

}
