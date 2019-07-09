package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.infra.dataobject.DataLogDTO;
import io.choerodon.agile.infra.dataobject.DataLogStatusChangeDO;
import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */

public interface DataLogMapper extends Mapper<DataLogDTO> {

    List selectByIssueId(@Param("projectId") Long projectId,
                         @Param("issueId") Long issueId);

    DataLogDTO selectLastWorkLogById(@Param("projectId") Long projectId,
                                     @Param("issueId") Long issueId,
                                     @Param("field") String field);

    /**
     * 批量生成issue是否解决日志
     *
     * @param projectId    projectId
     * @param issueDTOS     issueDTOS
     * @param userId       userId
     * @param statusMapVO statusMapVO
     * @param completed    completed
     */
    void batchCreateStatusLogByIssueDOS(@Param("projectId") Long projectId, @Param("issueDTOS") List<IssueDTO> issueDTOS
            , @Param("userId") Long userId, @Param("statusMapVO") StatusMapVO statusMapVO, @Param("completed") Boolean completed);

    /**
     * 批量生成issue状态变更日志
     *
     * @param projectId projectId
     * @param issueDTOS  issueDTOS
     * @param userId    userId
     * @param oldStatus oldStatus
     * @param newStatus newStatus
     */
    void batchCreateChangeStatusLogByIssueDOS(@Param("projectId") Long projectId, @Param("issueDTOS") List<IssueDTO> issueDTOS, @Param("userId") Long userId,
                                              @Param("oldStatus") StatusMapVO oldStatus, @Param("newStatus") StatusMapVO newStatus);


    /**
     * 批量生成版本变更日志
     *
     * @param projectId        projectId
     * @param productVersionDO productVersionDO
     * @param issueIds         issueIds
     * @param userId           userId
     */
    void batchCreateVersionDataLog(@Param("projectId") Long projectId, @Param("productVersionDO") ProductVersionDO productVersionDO, @Param("issueIds") List<Long> issueIds, @Param("userId") Long userId);

    /**
     * 批量删除错误数据
     *
     * @param dataLogIds dataLogIds
     */
    void batchDeleteErrorDataLog(@Param("dataLogIds") Set<Long> dataLogIds);

    /**
     * 更新脏数据
     *
     * @param dataLogStatusChangeDOS dataLogStatusChangeDOS
     */
    void batchUpdateErrorDataLog(@Param("dataLogStatusChangeDOS") Set<DataLogStatusChangeDO> dataLogStatusChangeDOS);

    void updateStatusRtDataLog(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("creationDate") Date creationDate, @Param("lastUpdateDate") Date lastUpdateDate, @Param("userId") Long userId);

    void updateExpStatusRtDataLog(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("creationDate") Date creationDate, @Param("lastUpdateDate") Date lastUpdateDate, @Param("userId") Long userId);

    void updateStatusDingDataLog(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("creationDate") Date creationDate, @Param("lastUpdateDate") Date lastUpdateDate, @Param("userId") Long userId);

    void updateDemoEpicDataLog(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("creationDate") Date creationDate, @Param("lastUpdateDate") Date lastUpdateDate, @Param("userId") Long userId);

    void batchCreateChangePriorityLogByIssueDOs(@Param("issueDTOS") List<IssueDTO> issueDTOS, @Param("userId") Long userId,
                                                @Param("oldPriorityName") String oldPriorityName, @Param("newPriorityName") String newPriorityName);
}
