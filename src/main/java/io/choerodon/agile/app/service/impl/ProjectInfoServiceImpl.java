package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.ProjectInfoVO;
import io.choerodon.agile.api.vo.ProjectRelationshipVO;
import io.choerodon.agile.api.vo.RoleAssignmentSearchVO;
import io.choerodon.agile.api.vo.UserWithRoleVO;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.api.vo.event.ProjectEvent;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.ProjectInfoDTO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private UserFeignClient userFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public void initializationProjectInfo(ProjectEvent projectEvent) {
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setIssueMaxNum(0L);
        projectInfoDTO.setProjectCode(projectEvent.getProjectCode());
        projectInfoDTO.setProjectId(projectEvent.getProjectId());
        projectInfoDTO.setFeedbackMaxNum(0L);
        int result = projectInfoMapper.insert(projectInfoDTO);
        if (result != 1) {
            throw new CommonException("error.projectInfo.initializationProjectInfo");
        }
    }

    @Override
    public Boolean checkProjectCode(String projectName) {
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setProjectCode(projectName);
        return projectInfoMapper.selectOne(projectInfoDTO) != null;
    }

    @Override
    public ProjectInfoVO updateProjectInfo(ProjectInfoVO projectInfoVO) {
        ProjectInfoDTO projectInfoDTO = modelMapper.map(projectInfoVO, ProjectInfoDTO.class);
        if (projectInfoMapper.updateByPrimaryKeySelective(projectInfoDTO) != 1) {
            throw new CommonException("error.projectInfo.update");
        }
        return projectInfoVO;
    }

    @Override
    public ProjectInfoVO queryProjectInfoByProjectId(Long projectId) {
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setProjectId(projectId);
        return modelMapper.map(projectInfoMapper.selectOne(projectInfoDTO), ProjectInfoVO.class);
    }

    @Override
    public List<ProjectRelationshipVO> queryProgramTeamInfo(Long projectId) {
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        List<ProjectRelationshipVO> projectRelationshipVOS = userFeignClient.getProjUnderGroup(organizationId, projectId, true).getBody();
        for (ProjectRelationshipVO relationshipVO : projectRelationshipVOS) {
            PageInfo<UserWithRoleVO> users = userFeignClient.pagingQueryUsersWithProjectLevelRoles(0, 0, relationshipVO.getProjectId(), new RoleAssignmentSearchVO(), false).getBody();
            relationshipVO.setUserCount(users.getSize());
        }
        return projectRelationshipVOS;
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

}
