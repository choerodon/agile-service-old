package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.SearchVO;
import io.choerodon.agile.infra.dataobject.FeedbackDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface FeedbackMapper extends Mapper<FeedbackDTO> {

    List<FeedbackDTO> selectByPage(@Param("projectId") Long projectId, @Param("searchVO") SearchVO searchVO);

    FeedbackDTO selectById(@Param("projectId") Long projectId, @Param("id") Long id);
}
