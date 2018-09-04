package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.AssigneeIssueDO;
import io.choerodon.agile.infra.dataobject.PriorityDistributeDO;
import io.choerodon.agile.infra.dataobject.StatusCategoryDO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.agile.infra.dataobject.AssigneeDistributeDO;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableMapper {

    List<PriorityDistributeDO> queryPriorityDistribute(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<StatusCategoryDO> queryStatusCategoryDistribute(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<AssigneeIssueDO> queryAssigneeInfoBySprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    Integer queryAssigneeAll(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<AssigneeDistributeDO> queryPriorityAssignee(@Param("projectId") Long projectId,
                                                     @Param("sprintId") Long sprintId,
                                                     @Param("total") Integer total);
}
