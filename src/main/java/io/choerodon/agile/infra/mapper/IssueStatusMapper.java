package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.IssueStatusCreateDO;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.IssueStatusDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueStatusMapper extends BaseMapper<IssueStatusDO> {

    List queryUnCorrespondStatus(@Param("projectId") Long projectId, @Param("boardId") Long boardId);

    /**
     * 根据项目id和categoryCode查询statusId
     *
     * @param projectId    projectId
     * @param categoryCode categoryCode
     * @return Long
     */
    Long queryIdByProjectIdAndCategoryCode(@Param("projectId") Long projectId, @Param("categoryCode") String categoryCode);

    /**
     * 根据项目id查询第一列的第一个状态
     *
     * @param projectId projectId
     * @return Long
     */
    List<IssueStatusCreateDO> queryIssueStatus(@Param("projectId") Long projectId);

    Integer checkSameStatus(@Param("projectId") Long projectId, @Param("statusName") String statusName);
}
