//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.domain.agile.entity.FileOperationHistoryE;
//import io.choerodon.agile.infra.dataobject.FileOperationHistoryDTO;
//import io.choerodon.agile.infra.repository.FileOperationHistoryRepository;
//import io.choerodon.agile.infra.mapper.FileOperationHistoryMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/2/25.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class FileOperationHistoryRepositoryImpl implements FileOperationHistoryRepository {
//
//    @Autowired
//    private FileOperationHistoryMapper fileOperationHistoryMapper;
//
//    @Override
//    public FileOperationHistoryE create(FileOperationHistoryE fileOperationHistoryE) {
//        FileOperationHistoryDTO fileOperationHistoryDTO = ConvertHelper.convert(fileOperationHistoryE, FileOperationHistoryDTO.class);
//        if (fileOperationHistoryMapper.insert(fileOperationHistoryDTO) != 1) {
//            throw new CommonException("error.FileOperationHistoryDTO.insert");
//        }
//        return ConvertHelper.convert(fileOperationHistoryMapper.selectByPrimaryKey(fileOperationHistoryDTO.getId()), FileOperationHistoryE.class);
//    }
//
//    @Override
//    public FileOperationHistoryE updateBySeletive(FileOperationHistoryE fileOperationHistoryE) {
//        FileOperationHistoryDTO fileOperationHistoryDTO = ConvertHelper.convert(fileOperationHistoryE, FileOperationHistoryDTO.class);
//        if (fileOperationHistoryMapper.updateByPrimaryKeySelective(fileOperationHistoryDTO) != 1) {
//            throw new CommonException("error.FileOperationHistoryDTO.update");
//        }
//        return ConvertHelper.convert(fileOperationHistoryMapper.selectByPrimaryKey(fileOperationHistoryDTO.getId()), FileOperationHistoryE.class);
//    }
//}
