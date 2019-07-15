//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.infra.common.utils.RedisUtil;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.agile.domain.agile.entity.IssueLabelE;
//import io.choerodon.agile.infra.repository.IssueLabelRepository;
//import io.choerodon.agile.infra.dataobject.IssueLabelDTO;
//import io.choerodon.agile.infra.mapper.IssueLabelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
///**
// * 敏捷开发Issue标签
// *
// * @author dinghuang123@gmail.com
// * @since 2018-05-14 21:04:00
// */
//@Component
//public class IssueLabelRepositoryImpl implements IssueLabelRepository {
//
//    private static final String INSERT_ERROR = "error.IssueLabel.insert";
//    private static final String AGILE = "Agile:";
//    private static final String LABEL = "label";
//    private static final String PIE_CHART = AGILE + "PieChart";
//
//    @Autowired
//    private IssueLabelMapper issueLabelMapper;
//    @Autowired
//    private RedisUtil redisUtil;
//
//    @Override
//    public IssueLabelE create(IssueLabelE issueLabelE) {
//        IssueLabelDTO issueLabelDTO = ConvertHelper.convert(issueLabelE, IssueLabelDTO.class);
//        if (issueLabelMapper.insert(issueLabelDTO) != 1) {
//            throw new CommonException(INSERT_ERROR);
//        }
//        redisUtil.deleteRedisCache(new String[]{PIE_CHART + issueLabelE.getProjectId() + ':' + LABEL + "*"});
//        return ConvertHelper.convert(issueLabelMapper.selectByPrimaryKey(issueLabelDTO.getLabelId()), IssueLabelE.class);
//    }
//
//    @Override
//    public int labelGarbageCollection(Long projectId) {
//        redisUtil.deleteRedisCache(new String[]{PIE_CHART + projectId + ':' + LABEL + "*"});
//        return issueLabelMapper.labelGarbageCollection(projectId);
//    }
//
//}