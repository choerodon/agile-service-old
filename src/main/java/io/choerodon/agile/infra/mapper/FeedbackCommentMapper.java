package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.FeedbackCommentDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface FeedbackCommentMapper extends Mapper<FeedbackCommentDTO> {

    List<FeedbackCommentDTO> selectByOptions(@Param("projectId") Long projectId, @Param("feedbackId") Long feedbackId);

}
