package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.WorkLogE;
import io.choerodon.agile.infra.repository.WorkLogRepository;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.WorkLogDO;
import io.choerodon.agile.infra.mapper.WorkLogMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class WorkLogRepositoryImpl implements WorkLogRepository {

    @Autowired
    private WorkLogMapper workLogMapper;

    @Override
    @DataLog(type = "createWorkLog")
    public WorkLogE create(WorkLogE workLogE) {
        WorkLogDO workLogDO = ConvertHelper.convert(workLogE, WorkLogDO.class);
        if (workLogMapper.insert(workLogDO) != 1) {
            throw new CommonException("error.workLog.insert");
        }
        return ConvertHelper.convert(workLogMapper.selectByPrimaryKey(workLogDO.getLogId()), WorkLogE.class);
    }

    @Override
    public WorkLogE update(WorkLogE workLogE) {
        WorkLogDO workLogDO = ConvertHelper.convert(workLogE, WorkLogDO.class);
        if (workLogMapper.updateByPrimaryKeySelective(workLogDO) != 1) {
            throw new CommonException("error.workLog.update");
        }
        return ConvertHelper.convert(workLogMapper.selectByPrimaryKey(workLogDO.getLogId()), WorkLogE.class);
    }

    @Override
    @DataLog(type = "deleteWorkLog")
    public void delete(Long projectId,Long logId) {
        WorkLogDO query = new WorkLogDO();
        query.setProjectId(projectId);
        query.setLogId(logId);
        WorkLogDO workLogDO = workLogMapper.selectOne(query);
        if (workLogDO == null) {
            throw new CommonException("error.workLog.get");
        }
        if (workLogMapper.delete(workLogDO) != 1) {
            throw new CommonException("error.workLog.delete");
        }
    }
}
