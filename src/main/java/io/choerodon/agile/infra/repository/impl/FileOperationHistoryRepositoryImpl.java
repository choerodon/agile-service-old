package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.FileOperationHistoryE;
import io.choerodon.agile.domain.agile.repository.FileOperationHistoryRepository;
import io.choerodon.agile.infra.dataobject.FileOperationHistoryDO;
import io.choerodon.agile.infra.mapper.FileOperationHistoryMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FileOperationHistoryRepositoryImpl implements FileOperationHistoryRepository {

    @Autowired
    private FileOperationHistoryMapper fileOperationHistoryMapper;

    @Override
    public FileOperationHistoryE create(FileOperationHistoryE fileOperationHistoryE) {
        FileOperationHistoryDO fileOperationHistoryDO = ConvertHelper.convert(fileOperationHistoryE, FileOperationHistoryDO.class);
        if (fileOperationHistoryMapper.insert(fileOperationHistoryDO) != 1) {
            throw new CommonException("error.FileOperationHistoryDO.insert");
        }
        return ConvertHelper.convert(fileOperationHistoryMapper.selectByPrimaryKey(fileOperationHistoryDO.getId()), FileOperationHistoryE.class);
    }

    @Override
    public FileOperationHistoryE updateBySeletive(FileOperationHistoryE fileOperationHistoryE) {
        FileOperationHistoryDO fileOperationHistoryDO = ConvertHelper.convert(fileOperationHistoryE, FileOperationHistoryDO.class);
        if (fileOperationHistoryMapper.updateByPrimaryKeySelective(fileOperationHistoryDO) != 1) {
            throw new CommonException("error.FileOperationHistoryDO.update");
        }
        return ConvertHelper.convert(fileOperationHistoryMapper.selectByPrimaryKey(fileOperationHistoryDO.getId()), FileOperationHistoryE.class);
    }
}
