package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.dto.StatusDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.domain.agile.event.AddStatusWithProject;
import io.choerodon.agile.domain.agile.event.ProjectConfig;
import io.choerodon.agile.infra.dataobject.IssueStatusCreateDO;
import io.choerodon.agile.infra.dataobject.StatusDO;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.IssueStatusDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueStatusMapper extends BaseMapper<IssueStatusDO> {

    //    List queryUnCorrespondStatus(@Param("projectId") Long projectId, @Param("boardId") Long boardId);
    List queryUnCorrespondStatus(@Param("projectId") Long projectId, @Param("boardId") Long boardId, @Param("realStatusIds") List<Long> realStatusIds);

    /**
     * 根据项目id查询第一列的第一个状态
     *
     * @param projectId projectId
     * @return Long
     */
//    List<IssueStatusCreateDO> queryIssueStatus(@Param("projectId") Long projectId);

//    Integer checkSameStatus(@Param("projectId") Long projectId, @Param("statusName") String statusName);

//    List<StatusDO> listByProjectId(@Param("projectId") Long projectId);

    List<IssueStatusDO> selectStatusIdIsNotNull();

    void batchUpdateStatus(@Param("statuses") List<IssueStatusDO> statuses);

    void updateAllStatusId();

    void updateAllColumnStatusId();

    void updateDataLogStatusId();

    IssueStatusDO selectByStatusId(@Param("projectId") Long projectId, @Param("statusId") Long statusId);

    /**
     * 批量创建状态
     *
     * @param addStatusWithProjects addStatusWithProjects
     * @param userId                userId
     */
    void batchCreateStatusByProjectIds(@Param("addStatusWithProjects") List<AddStatusWithProject> addStatusWithProjects, @Param("userId") Long userId);
}
