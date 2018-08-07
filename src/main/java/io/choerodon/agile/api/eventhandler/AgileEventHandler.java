package io.choerodon.agile.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.app.service.BoardService;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.event.ProjectEvent;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AgileEventHandler.class);

    private void loggerInfo(Object o) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("data: {}", o);
        }
    }

    /**
     * 创建项目事件
     *
     * @param data data
     */
    @SagaTask(code = "agileInitProject",
            description = "agile消费创建项目事件初始化项目数据",
            sagaCode = "iam-create-project",
            seq = 1)
    public String handleProjectInitByConsumeSagaTask(String data) {
        ProjectEvent projectEvent = JSONObject.parseObject(data, ProjectEvent.class);
        loggerInfo(projectEvent);
        boardService.initBoard(projectEvent.getProjectId(), projectEvent.getProjectName() + BOARD);
        projectInfoService.initializationProjectInfo(projectEvent);
        issueLinkTypeService.initIssueLinkType(projectEvent.getProjectId());
        return data;
    }

}
