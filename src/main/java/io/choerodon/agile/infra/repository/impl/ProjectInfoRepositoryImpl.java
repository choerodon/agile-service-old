package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.infra.repository.ProjectInfoRepository;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
@Component
public class ProjectInfoRepositoryImpl implements ProjectInfoRepository {

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public ProjectInfoE create(ProjectInfoE projectInfoE) {
        ProjectInfoDO projectInfoDO = ConvertHelper.convert(projectInfoE, ProjectInfoDO.class);
        int result = projectInfoMapper.insert(projectInfoDO);
        if (result != 1) {
            throw new CommonException("error.projectInfo.initializationProjectInfo");
        }
        ProjectInfoDO query = new ProjectInfoDO();
        query.setProjectId(projectInfoDO.getProjectId());
        return ConvertHelper.convert(projectInfoMapper.selectOne(query), ProjectInfoE.class);
    }

    /**
     * 更新MaxNum方法，在高并发的情况下，可能更新的maxNum已经不是最大的maxNum，因此不需要判断是否更新成功
     *
     * @param projectId   projectId
     * @param issueMaxNum issueMaxNum
     */
    @Override
    public void updateIssueMaxNum(Long projectId, String issueMaxNum) {
        projectInfoMapper.updateIssueMaxNum(projectId, issueMaxNum);
    }

    @Override
    public ProjectInfoE update(ProjectInfoE projectInfoE) {
        ProjectInfoDO projectInfoDO = ConvertHelper.convert(projectInfoE, ProjectInfoDO.class);
        if (projectInfoMapper.updateByPrimaryKeySelective(projectInfoDO) != 1) {
            throw new CommonException("error.projectInfo.update");
        }
        ProjectInfoDO query = new ProjectInfoDO();
        query.setProjectId(projectInfoDO.getProjectId());
        return ConvertHelper.convert(projectInfoMapper.selectOne(query), ProjectInfoE.class);
    }
}
