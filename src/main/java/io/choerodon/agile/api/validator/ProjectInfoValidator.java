package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.ProjectInfoVO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ProjectInfoValidator {

    public void verifyUpdateData(ProjectInfoVO projectInfoVO, Long projectId) {
        projectInfoVO.setProjectId(projectId);
        if (projectInfoVO.getInfoId() == null) {
            throw new CommonException("error.projectInfo.infoId");
        }
    }

}
