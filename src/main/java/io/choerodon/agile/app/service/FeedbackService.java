package io.choerodon.agile.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.FeedbackUpdateVO;
import io.choerodon.agile.api.vo.SearchVO;
import io.choerodon.agile.infra.dataobject.FeedbackDTO;


import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface FeedbackService {

    FeedbackDTO createFeedback(FeedbackDTO feedbackDTO);

    FeedbackDTO updateFeedback(Long projectId, FeedbackUpdateVO feedbackUpdateVO, List<String> fieldList);

    PageInfo<FeedbackDTO> queryFeedbackByPage(Long projectId, int page, int size, SearchVO searchVO);

    FeedbackDTO queryFeedbackById(Long projectId, Long organizationId, Long id);

    FeedbackDTO createBase(FeedbackDTO feedbackDTO);

}
