package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.*;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableMapper {

    List<PriorityDistributeDTO> queryPriorityDistribute(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<StatusCategoryDTO> queryStatusCategoryDistribute(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<AssigneeIssueDTO> queryAssigneeInfoBySprintId(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    Integer queryAssigneeAll(@Param("projectId") Long projectId, @Param("sprintId") Long sprintId);

    List<AssigneeDistributeDTO> queryAssigneeDistribute(@Param("projectId") Long projectId,
                                                        @Param("sprintId") Long sprintId,
                                                        @Param("total") Integer total);

    List<IssueTypeDistributeDTO> queryIssueTypeDistribute(@Param("projectId") Long projectId,
                                                          @Param("sprintId") Long sprintId);
}
