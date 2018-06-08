package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.repository.ProjectInfoRepository;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
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
public class ProjectInfoRepositoryImpl implements ProjectInfoRepository {

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public ProjectInfoDO create(ProjectInfoDO projectInfoDO) {
        int result = projectInfoMapper.insert(projectInfoDO);
        if (result != 1) {
            throw new CommonException("error.projectInfo.initializationProjectInfo");
        }
        ProjectInfoDO query = new ProjectInfoDO();
        query.setProjectId(projectInfoDO.getProjectId());
        return projectInfoMapper.selectOne(query);
    }

    @Override
    public int updateIssueMaxNum(Long projectId) {
        int result = projectInfoMapper.updateIssueMaxNum(projectId);
        if (result != 1) {
            throw new CommonException("error.projectInfo.updateIssueMaxNum");
        }
        return result;
    }
}
