package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.DataLogDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */

public interface DataLogMapper extends BaseMapper<DataLogDO> {

    List selectByIssueId(@Param("projectId") Long projectId,
                         @Param("issueId") Long issueId);

    DataLogDO selectLastWorkLogById(@Param("projectId") Long projectId,
                                    @Param("issueId") Long issueId,
                                    @Param("filed") String filed);
}
