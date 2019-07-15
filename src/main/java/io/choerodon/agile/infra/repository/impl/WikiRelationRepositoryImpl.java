//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.domain.agile.entity.WikiRelationE;
//import io.choerodon.agile.infra.dataobject.WikiRelationDTO;
//import io.choerodon.agile.infra.repository.WikiRelationRepository;
//import io.choerodon.agile.infra.mapper.WikiRelationMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/12/03.
// * Email: fuqianghuang01@gmail.com
// */
//@Service
//public class WikiRelationRepositoryImpl implements WikiRelationRepository {
//
//    @Autowired
//    private WikiRelationMapper wikiRelationMapper;
//
//    @Override
//    public void create(WikiRelationE wikiRelationE) {
//        WikiRelationDTO wikiRelationDTO = ConvertHelper.convert(wikiRelationE, WikiRelationDTO.class);
//        if (wikiRelationMapper.insert(wikiRelationDTO) != 1) {
//            throw new CommonException("error.wikiRelationDTO.insert");
//        }
//    }
//
//    @Override
//    public void delete(WikiRelationE wikiRelationE) {
//        WikiRelationDTO wikiRelationDTO = ConvertHelper.convert(wikiRelationE, WikiRelationDTO.class);
//        if (wikiRelationMapper.delete(wikiRelationDTO) != 1) {
//            throw new CommonException("error.wikiRelationDTO.delete");
//        }
//    }
//}
