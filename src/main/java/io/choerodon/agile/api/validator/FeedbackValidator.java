package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.FeedbackUpdateVO;
import io.choerodon.agile.infra.dataobject.FeedbackDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeedbackValidator {

    public void checkFeedbackCreate(FeedbackDTO feedbackDTO) {
        if (feedbackDTO.getSummary() == null) {
            throw new CommonException("error.summary.isNull");
        }
        if (feedbackDTO.getType() == null) {
            throw new CommonException("error.type.isNull");
        }
        if (feedbackDTO.getToken() == null) {
            throw new CommonException("error.token.isNull");
        }
    }

    public void checkFeedbackUpdate(FeedbackUpdateVO feedbackUpdateVO) {
        if (feedbackUpdateVO.getId() == null) {
            throw new CommonException("error.id.isNull");
        }
        if (feedbackUpdateVO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.isNull");
        }
    }
}
