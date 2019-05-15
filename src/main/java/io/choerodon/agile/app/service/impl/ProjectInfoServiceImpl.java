package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.dto.ProjectInfoDTO;
import io.choerodon.agile.api.dto.ProjectRelationshipDTO;
import io.choerodon.agile.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.agile.api.dto.UserWithRoleDTO;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.domain.agile.event.ProjectEvent;
import io.choerodon.agile.infra.repository.ProjectInfoRepository;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired
    private ProjectInfoRepository projectInfoRepository;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public void initializationProjectInfo(ProjectEvent projectEvent) {
        ProjectInfoE projectInfoE = new ProjectInfoE();
        projectInfoE.setIssueMaxNum(0L);
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

    @Override
    public List<ProjectRelationshipDTO> queryProgramTeamInfo(Long projectId) {
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        List<ProjectRelationshipDTO> projectRelationshipDTOs = userFeignClient.getProjUnderGroup(organizationId, projectId, true).getBody();
        for (ProjectRelationshipDTO relationshipDTO : projectRelationshipDTOs) {
            PageInfo<UserWithRoleDTO> users = userFeignClient.pagingQueryUsersWithProjectLevelRoles(0, 0, relationshipDTO.getProjectId(), new RoleAssignmentSearchDTO(), false).getBody();
            relationshipDTO.setUserCount(users.getSize());
        }
        return projectRelationshipDTOs;
    }
}
