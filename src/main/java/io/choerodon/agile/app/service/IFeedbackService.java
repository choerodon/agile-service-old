package io.choerodon.agile.app.service;


import io.choerodon.agile.api.vo.FeedbackUpdateVO;
import io.choerodon.agile.infra.dataobject.FeedbackDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface IFeedbackService {

    FeedbackDTO updateBase(FeedbackUpdateVO feedbackUpdateVO, String[] fieldList);
}
