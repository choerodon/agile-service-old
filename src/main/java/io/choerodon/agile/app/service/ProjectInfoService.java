package io.choerodon.agile.app.service;

import io.choerodon.agile.domain.agile.event.ProjectEvent;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
public interface ProjectInfoService {

    /**
     * 初始化projectInfo，用于维护issue编号项目基准值
     *
     * @param projectEvent projectEvent
     */
    void initializationProjectInfo(ProjectEvent projectEvent);
}
