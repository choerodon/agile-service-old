package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.PriorityDistributeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableMapper {

    List<PriorityDistributeDO> queryPriorityDistribute(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

}
