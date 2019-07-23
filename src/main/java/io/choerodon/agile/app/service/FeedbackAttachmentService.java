package io.choerodon.agile.app.service;


import io.choerodon.agile.infra.dataobject.FeedbackAttachmentDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface FeedbackAttachmentService {

    List<FeedbackAttachmentDTO> create(Long projectId, Long feedbackId, Long commentId, HttpServletRequest request);

    Boolean delete(Long projectId, Long id);

    List<String> uploadForAddress(Long projectId, HttpServletRequest request);

    List<FeedbackAttachmentDTO> uploadAttachmentPublic(Long feedbackId, String token, HttpServletRequest request);

}
