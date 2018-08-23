package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.domain.agile.repository.DataLogRepository;
import io.choerodon.agile.infra.common.annotation.RedisCache;
import io.choerodon.agile.infra.dataobject.DataLogDO;
import io.choerodon.agile.infra.mapper.DataLogMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DataLogRepositoryImpl implements DataLogRepository {

    @Autowired
    private DataLogMapper dataLogMapper;

    @Override
    @CacheEvict(key = "'cumulativeFlow'+'@dataLogE.projectId'")
    @RedisCache(cacheNames = {"burnDownCoordinate"}, operation = RedisCache.CACHE_OPERATION.REMOVE, ttl = 3600, projectId = "#dataLogE.projectId")
    public DataLogE create(DataLogE dataLogE) {
        DataLogDO dataLogDO = ConvertHelper.convert(dataLogE, DataLogDO.class);
        if (dataLogMapper.insert(dataLogDO) != 1) {
            throw new CommonException("error.dataLog.insert");
        }
        return ConvertHelper.convert(dataLogMapper.selectByPrimaryKey(dataLogDO.getLogId()), DataLogE.class);
    }

    @Override
    public void delete(DataLogE dataLogE) {
        DataLogDO dataLogDO = ConvertHelper.convert(dataLogE, DataLogDO.class);
        dataLogMapper.delete(dataLogDO);
    }

}
