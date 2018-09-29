package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.WorkLogDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
public interface WorkLogMapper extends BaseMapper<WorkLogDO> {

    /**
     * 根据issueId 倒序查找WorkLog
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return WorkLogDO
     */
    List<WorkLogDO> queryByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);
}
