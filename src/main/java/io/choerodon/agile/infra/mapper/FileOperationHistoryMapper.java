package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.FileOperationHistoryDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
public interface FileOperationHistoryMapper extends BaseMapper<FileOperationHistoryDO> {

    FileOperationHistoryDO queryLatestRecode(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
