//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.FeatureVO;
//import io.choerodon.agile.domain.agile.entity.FeatureE;
//import io.choerodon.agile.infra.dataobject.FeatureDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/3/13.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class FeatureConverter implements ConvertorI<FeatureE, FeatureVO, FeatureDTO> {
//
//    @Override
//    public FeatureE dtoToEntity(FeatureDTO featureDTO) {
//        FeatureE featureE = new FeatureE();
//        BeanUtils.copyProperties(featureDTO, featureE);
//        return featureE;
//    }
//
//    @Override
//    public FeatureDTO entityToDto(FeatureE featureE) {
//        FeatureDTO featureDTO = new FeatureDTO();
//        BeanUtils.copyProperties(featureE, featureDTO);
//        return featureDTO;
//    }
//
//    @Override
//    public FeatureE doToEntity(FeatureVO featureVO) {
//        FeatureE featureE = new FeatureE();
//        BeanUtils.copyProperties(featureVO, featureE);
//        return featureE;
//    }
//
//    @Override
//    public FeatureVO entityToDo(FeatureE featureE) {
//        FeatureVO featureVO = new FeatureVO();
//        BeanUtils.copyProperties(featureE, featureVO);
//        return featureVO;
//    }
//
//    @Override
//    public FeatureDTO doToDto(FeatureVO featureVO) {
//        FeatureDTO featureDTO = new FeatureDTO();
//        BeanUtils.copyProperties(featureVO, featureDTO);
//        return featureDTO;
//    }
//
//    @Override
//    public FeatureVO dtoToDo(FeatureDTO featureDTO) {
//        FeatureVO featureVO = new FeatureVO();
//        BeanUtils.copyProperties(featureDTO, featureVO);
//        return featureVO;
//    }
//}
