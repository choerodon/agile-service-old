//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.domain.agile.entity.DataLogE;
//import io.choerodon.agile.infra.repository.DataLogRepository;
//import io.choerodon.agile.infra.dataobject.DataLogDTO;
//import io.choerodon.agile.infra.dataobject.DataLogStatusChangeDTO;
//import io.choerodon.agile.infra.mapper.DataLogMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Set;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/6/14.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//@Transactional(rollbackFor = Exception.class)
//public class DataLogRepositoryImpl implements DataLogRepository {
//
//    @Autowired
//    private DataLogMapper dataLogMapper;
//
//    @Override
//    public DataLogE create(DataLogE dataLogE) {
//        DataLogDTO dataLogDTO = ConvertHelper.convert(dataLogE, DataLogDTO.class);
//        if (dataLogMapper.insert(dataLogDTO) != 1) {
//            throw new CommonException("error.dataLog.insert");
//        }
//        return ConvertHelper.convert(dataLogMapper.selectByPrimaryKey(dataLogDTO.getLogId()), DataLogE.class);
//    }
//
//    @Override
//    public void delete(DataLogE dataLogE) {
//        DataLogDTO dataLogDTO = ConvertHelper.convert(dataLogE, DataLogDTO.class);
//        dataLogMapper.delete(dataLogDTO);
//    }
//
//    @Override
//    public void batchDeleteErrorDataLog(Set<Long> dataLogIds) {
//        dataLogMapper.batchDeleteErrorDataLog(dataLogIds);
//    }
//
//    @Override
//    public void batchUpdateErrorDataLog(Set<DataLogStatusChangeDTO> dataLogStatusChangeDTOS) {
//        dataLogMapper.batchUpdateErrorDataLog(dataLogStatusChangeDTOS);
//    }
//
//}
