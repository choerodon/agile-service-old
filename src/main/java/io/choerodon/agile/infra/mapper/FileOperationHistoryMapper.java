package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.FileOperationHistoryDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
public interface FileOperationHistoryMapper extends Mapper<FileOperationHistoryDTO> {

    FileOperationHistoryDTO queryLatestRecode(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
