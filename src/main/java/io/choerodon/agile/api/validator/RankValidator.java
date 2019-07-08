package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.RankDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/24.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class RankValidator {

    public void checkEpicAndFeatureRank(RankDTO rankDTO) {
        if (rankDTO.getReferenceIssueId() == null) {
            throw new CommonException("error.referenceIssueId.isNull");
        }
        if (rankDTO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (rankDTO.getType() == null) {
            throw new CommonException("error.type.isNull");
        }
        if (rankDTO.getBefore() == null) {
            throw new CommonException("error.before.isNull");
        }
        if (rankDTO.getIssueId() == null) {
            throw new CommonException("error.issueId.isNull");
        }
    }
}
