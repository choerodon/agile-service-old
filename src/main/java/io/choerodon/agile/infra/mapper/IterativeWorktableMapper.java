package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.*;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableMapper {

    List<PriorityDistributeDO> queryPriorityDistribute(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<StatusCategoryDO> queryStatusCategoryDistribute(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<AssigneeIssueDO> queryAssigneeInfoBySprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    Integer queryAssigneeAll(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<AssigneeDistributeDO> queryAssigneeDistribute(@Param("projectId") Long projectId,
                                                     @Param("sprintId") Long sprintId,
                                                     @Param("total") Integer total);

    List<IssueTypeDistributeDO> queryIssueTypeDistribute(@Param("projectId") Long projectId,
                                                         @Param("sprintId") Long sprintId);
}
