//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.infra.common.utils.RedisUtil;
//import io.choerodon.agile.infra.dataobject.ColumnStatusRelDTO;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
//import io.choerodon.agile.infra.repository.ColumnStatusRelRepository;
//import io.choerodon.agile.infra.mapper.ColumnStatusRelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/16.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class ColumnStatusRelRepositoryImpl implements ColumnStatusRelRepository {
//
//    @Autowired
//    private ColumnStatusRelMapper columnStatusRelMapper;
//    @Autowired
//    private RedisUtil redisUtil;
//
//    @Override
//    public void create(ColumnStatusRelE columnStatusRelE) {
//        ColumnStatusRelDTO columnStatusRelDTO = ConvertHelper.convert(columnStatusRelE, ColumnStatusRelDTO.class);
//        if (columnStatusRelMapper.insert(columnStatusRelDTO) != 1) {
//            throw new CommonException("error.ColumnStatus.insert");
//        }
//        redisUtil.deleteRedisCache(new String[]{"Agile:CumulativeFlowDiagram" + columnStatusRelE.getProjectId() + ':' + "*"});
//    }
//
//    @Override
//    public void delete(ColumnStatusRelE columnStatusRelE) {
//        ColumnStatusRelDTO columnStatusRelDTO = ConvertHelper.convert(columnStatusRelE, ColumnStatusRelDTO.class);
//        if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
//            return;
//        }
//        if (columnStatusRelMapper.delete(columnStatusRelDTO) == 0) {
//            throw new CommonException("error.ColumnStatus.delete");
//        }
//        redisUtil.deleteRedisCache(new String[]{"Agile:CumulativeFlowDiagram" + columnStatusRelE.getProjectId() + ':' + "*"});
//    }
//
//}
