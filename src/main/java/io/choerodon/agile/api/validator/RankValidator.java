package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.dto.RankDTO;
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
    }
}
