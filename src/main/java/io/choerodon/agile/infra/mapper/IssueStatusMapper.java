package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.event.AddStatusWithProject;
import io.choerodon.mybatis.common.Mapper;
import io.choerodon.agile.infra.dataobject.IssueStatusDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueStatusMapper extends Mapper<IssueStatusDTO> {

    List queryUnCorrespondStatus(@Param("projectId") Long projectId, @Param("boardId") Long boardId, @Param("realStatusIds") List<Long> realStatusIds);

    List<IssueStatusDTO> selectStatusIdIsNotNull();

    void batchUpdateStatus(@Param("statuses") List<IssueStatusDTO> statuses);

    void updateAllStatusId();

    void updateAllColumnStatusId();

    void updateDataLogStatusId();

    IssueStatusDTO selectByStatusId(@Param("projectId") Long projectId, @Param("statusId") Long statusId);

    /**
     * 批量创建状态
     *
     * @param addStatusWithProjects addStatusWithProjects
     * @param userId                userId
     */
    void batchCreateStatusByProjectIds(@Param("addStatusWithProjects") List<AddStatusWithProject> addStatusWithProjects, @Param("userId") Long userId);
}
