package io.choerodon.agile.app.service;


import io.choerodon.agile.infra.dataobject.FeedbackDataLogDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface FeedbackDataLogService {

    List<FeedbackDataLogDTO> listByFeedbackId(Long projectId, Long feedbackId);
}
