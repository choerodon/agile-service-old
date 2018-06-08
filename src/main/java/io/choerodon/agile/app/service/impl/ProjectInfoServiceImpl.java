package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.event.ProjectEvent;
import io.choerodon.agile.domain.agile.repository.ProjectInfoRepository;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
@Component
@Transactional(rollbackFor = CommonException.class)
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Override
    public void initializationProjectInfo(ProjectEvent projectEvent) {
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setIssueMaxNum(1L);
        projectInfoDO.setProjectCode(projectEvent.getProjectCode());
        projectInfoDO.setProjectId(projectEvent.getProjectId());
        projectInfoRepository.create(projectInfoDO);
    }
}
