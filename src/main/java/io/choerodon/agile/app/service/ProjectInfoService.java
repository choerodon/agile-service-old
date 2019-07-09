package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.ProjectInfoVO;
import io.choerodon.agile.api.vo.ProjectRelationshipVO;
import io.choerodon.agile.api.vo.event.ProjectEvent;

import java.util.List;

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

    /**
     * 项目code重名校验
     *
     * @param projectName projectName
     * @return Boolean 存在返回true，否则false
     */
    Boolean checkProjectCode(String projectName);

    /**
     * 更新项目信息
     *
     * @param projectInfoVO projectInfoVO
     * @return ProjectInfoVO
     */
    ProjectInfoVO updateProjectInfo(ProjectInfoVO projectInfoVO);

    /**
     * 查询项目信息
     *
     * @param projectId projectId
     * @return ProjectInfoVO
     */
    ProjectInfoVO queryProjectInfoByProjectId(Long projectId);

    /**
     * 获取项目群关联的团队项目信息
     *
     * @param projectId
     * @return
     */
    List<ProjectRelationshipVO> queryProgramTeamInfo(Long projectId);
}
