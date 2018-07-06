package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.ProjectDefaultSettingDTO;
import io.choerodon.agile.api.dto.ProjectInfoDTO;
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
     * @param projectInfoDTO projectInfoDTO
     * @return ProjectInfoDTO
     */
    ProjectInfoDTO updateProjectInfo(ProjectInfoDTO projectInfoDTO);

    /**
     * 查询项目信息
     *
     * @param projectId projectId
     * @return ProjectInfoDTO
     */
    ProjectInfoDTO queryProjectInfoByProjectId(Long projectId);

}
