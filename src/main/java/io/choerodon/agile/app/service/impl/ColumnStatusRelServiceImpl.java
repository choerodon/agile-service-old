package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.ColumnStatusRelService;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.ColumnStatusRelDTO;
import io.choerodon.agile.infra.mapper.ColumnStatusRelMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/9.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class ColumnStatusRelServiceImpl implements ColumnStatusRelService {

    @Autowired
    private ColumnStatusRelMapper columnStatusRelMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void create(ColumnStatusRelDTO columnStatusRelDTO) {
        if (columnStatusRelMapper.insert(columnStatusRelDTO) != 1) {
            throw new CommonException("error.ColumnStatus.insert");
        }
        redisUtil.deleteRedisCache(new String[]{"Agile:CumulativeFlowDiagram" + columnStatusRelDTO.getProjectId() + ':' + "*"});
    }

    @Override
    public void delete(ColumnStatusRelDTO columnStatusRelDTO) {
        if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
            return;
        }
        if (columnStatusRelMapper.delete(columnStatusRelDTO) == 0) {
            throw new CommonException("error.ColumnStatus.delete");
        }
        redisUtil.deleteRedisCache(new String[]{"Agile:CumulativeFlowDiagram" + columnStatusRelDTO.getProjectId() + ':' + "*"});
    }

}
