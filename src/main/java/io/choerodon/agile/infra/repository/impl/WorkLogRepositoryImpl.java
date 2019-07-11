//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.domain.agile.entity.WorkLogE;
//import io.choerodon.agile.infra.dataobject.WorkLogDTO;
//import io.choerodon.agile.infra.repository.WorkLogRepository;
//import io.choerodon.agile.infra.common.annotation.DataLog;
//import io.choerodon.agile.infra.mapper.WorkLogMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/18.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class WorkLogRepositoryImpl implements WorkLogRepository {
//
//    @Autowired
//    private WorkLogMapper workLogMapper;
//
//    @Override
//    @DataLog(type = "createWorkLog")
//    public WorkLogE create(WorkLogE workLogE) {
//        WorkLogDTO workLogDTO = ConvertHelper.convert(workLogE, WorkLogDTO.class);
//        if (workLogMapper.insert(workLogDTO) != 1) {
//            throw new CommonException("error.workLog.insert");
//        }
//        return ConvertHelper.convert(workLogMapper.selectByPrimaryKey(workLogDTO.getLogId()), WorkLogE.class);
//    }
//
//    @Override
//    public WorkLogE update(WorkLogE workLogE) {
//        WorkLogDTO workLogDTO = ConvertHelper.convert(workLogE, WorkLogDTO.class);
//        if (workLogMapper.updateByPrimaryKeySelective(workLogDTO) != 1) {
//            throw new CommonException("error.workLog.update");
//        }
//        return ConvertHelper.convert(workLogMapper.selectByPrimaryKey(workLogDTO.getLogId()), WorkLogE.class);
//    }
//
//    @Override
//    @DataLog(type = "deleteWorkLog")
//    public void delete(Long projectId,Long logId) {
//        WorkLogDTO query = new WorkLogDTO();
//        query.setProjectId(projectId);
//        query.setLogId(logId);
//        WorkLogDTO workLogDTO = workLogMapper.selectOne(query);
//        if (workLogDTO == null) {
//            throw new CommonException("error.workLog.get");
//        }
//        if (workLogMapper.delete(workLogDTO) != 1) {
//            throw new CommonException("error.workLog.delete");
//        }
//    }
//}
