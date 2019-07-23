package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.FeedbackUpdateVO;
import io.choerodon.agile.app.service.IFeedbackService;
import io.choerodon.agile.infra.common.annotation.FeedbackDataLog;
import io.choerodon.agile.infra.dataobject.FeedbackDTO;
import io.choerodon.agile.infra.mapper.FeedbackMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.entity.Criteria;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class IFeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    @FeedbackDataLog(type = "feedback")
    public FeedbackDTO updateBase(FeedbackUpdateVO feedbackUpdateVO, String[] fieldList) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        BeanUtils.copyProperties(feedbackUpdateVO, feedbackDTO);
        Criteria criteria = new Criteria();
        criteria.update(fieldList);
        if (feedbackMapper.updateByPrimaryKeyOptions(feedbackDTO, criteria) != 1) {
            throw new CommonException("error.feedback.update");
        }
        return feedbackMapper.selectByPrimaryKey(feedbackDTO.getId());
    }

}
