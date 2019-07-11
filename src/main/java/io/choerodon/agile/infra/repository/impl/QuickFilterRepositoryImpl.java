//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.domain.agile.entity.QuickFilterE;
//import io.choerodon.agile.infra.dataobject.QuickFilterDTO;
//import io.choerodon.agile.infra.repository.QuickFilterRepository;
//import io.choerodon.agile.infra.mapper.QuickFilterMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/6/13.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class QuickFilterRepositoryImpl implements QuickFilterRepository {
//
//    @Autowired
//    private QuickFilterMapper quickFilterMapper;
//
//    @Override
//    public QuickFilterE create(QuickFilterE quickFilterE) {
//        QuickFilterDTO quickFilterDTO = ConvertHelper.convert(quickFilterE, QuickFilterDTO.class);
//        if (quickFilterMapper.insert(quickFilterDTO) != 1) {
//            throw new CommonException("error.quickFilter.insert");
//        }
//        return ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(quickFilterDTO.getFilterId()), QuickFilterE.class);
//    }
//
//    @Override
//    public QuickFilterE update(QuickFilterE quickFilterE) {
//        QuickFilterDTO quickFilterDTO = ConvertHelper.convert(quickFilterE, QuickFilterDTO.class);
//        if (quickFilterMapper.updateByPrimaryKeySelective(quickFilterDTO) != 1) {
//            throw new CommonException("error.quickFilter.update");
//        }
//        return ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(quickFilterDTO.getFilterId()), QuickFilterE.class);
//    }
//
//    @Override
//    public void deleteById(Long filterId) {
//        QuickFilterDTO quickFilterDTO = quickFilterMapper.selectByPrimaryKey(filterId);
//        if (quickFilterDTO == null) {
//            throw new CommonException("error.quickFilter.get");
//        }
//        if (quickFilterMapper.deleteByPrimaryKey(filterId) != 1) {
//            throw new CommonException("error.quickFilter.delete");
//        }
//    }
//
//    @Override
//    public int batchUpdateSequence(Integer sequence, Long projectId, Integer add,Long filterId) {
//        return quickFilterMapper.batchUpdateSequence(sequence, projectId, add,filterId);
//    }
//
//}
