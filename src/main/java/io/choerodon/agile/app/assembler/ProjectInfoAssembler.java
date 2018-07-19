package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.ProjectDefaultSettingDTO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
@Component
public class ProjectInfoAssembler {

    private static final String DEFAULT_ASSIGNEE = "default_assignee";
    private static final String CURRENT_USER = "current_user";

    public ProjectDefaultSettingDTO projectInfoDoToDto(ProjectInfoDO projectInfoDO) {
        ProjectDefaultSettingDTO projectDefaultSettingDTO = new ProjectDefaultSettingDTO();
        projectDefaultSettingDTO.setInfoId(projectInfoDO.getInfoId());
        projectDefaultSettingDTO.setProjectId(projectInfoDO.getProjectId());
        if (projectInfoDO.getDefaultAssigneeType() != null) {
            if (DEFAULT_ASSIGNEE.equals(projectInfoDO.getDefaultAssigneeType())) {
                projectDefaultSettingDTO.setDefaultAssigneeId(projectInfoDO.getDefaultAssigneeId());
            } else if (CURRENT_USER.equals(projectInfoDO.getDefaultAssigneeType())) {
                projectDefaultSettingDTO.setDefaultAssigneeId(DetailsHelper.getUserDetails().getUserId());
            }
        }
        if (projectInfoDO.getDefaultPriorityCode() != null) {
            projectDefaultSettingDTO.setDefaultPriorityCode(projectInfoDO.getDefaultPriorityCode());
        }
        return projectDefaultSettingDTO;
    }
}
