package io.choerodon.agile.domain.agile.rule;

import io.choerodon.agile.api.dto.ProjectInfoDTO;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/15
 */
@Component
public class ProjectInfoRule {

    @Autowired
    private ProjectInfoService projectInfoService;

    public void verifyUpdateData(ProjectInfoDTO projectInfoDTO, Long projectId) {
        projectInfoDTO.setProjectId(projectId);
        if (projectInfoDTO.getInfoId() == null) {
            throw new CommonException("error.projectInfo.infoId");
        }
        if (projectInfoDTO.getProjectCode() != null) {
            if (projectInfoService.checkProjectCode(projectInfoDTO.getProjectCode())) {
                throw new CommonException("error.projectInfo.checkProjectCode");
            }
        }
    }
}
