package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.RankVO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/24.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class RankValidator {

    public void checkEpicAndFeatureRank(RankVO rankVO) {
        if (rankVO.getReferenceIssueId() == null) {
            throw new CommonException("error.referenceIssueId.isNull");
        }
        if (rankVO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (rankVO.getType() == null) {
            throw new CommonException("error.type.isNull");
        }
        if (rankVO.getBefore() == null) {
            throw new CommonException("error.before.isNull");
        }
        if (rankVO.getIssueId() == null) {
            throw new CommonException("error.issueId.isNull");
        }
    }
}
