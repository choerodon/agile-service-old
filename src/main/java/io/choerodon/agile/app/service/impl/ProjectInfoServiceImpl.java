package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.ProjectInfoDTO;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.domain.agile.event.ProjectEvent;
import io.choerodon.agile.domain.agile.repository.ProjectInfoRepository;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.convertor.ConvertHelper;
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
    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public void initializationProjectInfo(ProjectEvent projectEvent) {
        ProjectInfoE projectInfoE = new ProjectInfoE();
        projectInfoE.setIssueMaxNum(1L);
        projectInfoE.setProjectCode(projectEvent.getProjectCode());
        projectInfoE.setProjectId(projectEvent.getProjectId());
        projectInfoRepository.create(projectInfoE);
    }

    @Override
    public Boolean checkProjectCode(String projectName) {
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectCode(projectName);
        return projectInfoMapper.selectOne(projectInfoDO) != null;
    }

    @Override
    public ProjectInfoDTO updateProjectInfo(ProjectInfoDTO projectInfoDTO) {
        return ConvertHelper.convert(projectInfoRepository.update(
                ConvertHelper.convert(projectInfoDTO, ProjectInfoE.class)), ProjectInfoDTO.class);
    }

    @Override
    public ProjectInfoDTO queryProjectInfoByProjectId(Long projectId) {
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(projectId);
        return ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoDTO.class);
    }
}
