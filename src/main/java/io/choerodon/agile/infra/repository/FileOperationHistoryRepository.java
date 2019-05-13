package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.FileOperationHistoryE;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
public interface FileOperationHistoryRepository {

    FileOperationHistoryE create(FileOperationHistoryE fileOperationHistoryE);

    FileOperationHistoryE updateBySeletive(FileOperationHistoryE fileOperationHistoryE);
}
