package io.choerodon.agile.domain.agile.rule;

import io.choerodon.agile.api.dto.ProjectInfoDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/15
 */
@Component
public class ProjectInfoRule {


    public void verifyUpdateData(ProjectInfoDTO projectInfoDTO, Long projectId) {
        projectInfoDTO.setProjectId(projectId);
        if (projectInfoDTO.getInfoId() == null) {
            throw new CommonException("error.projectInfo.infoId");
        }
    }
}
