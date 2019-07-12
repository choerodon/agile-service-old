//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.FeatureCommonVO;
//import io.choerodon.agile.infra.dataobject.FeatureCommonDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/4/9.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class FeatureCommonConverter implements ConvertorI<Object, FeatureCommonDTO, FeatureCommonVO> {
//
//    @Override
//    public FeatureCommonVO doToDto(FeatureCommonDTO featureCommonDTO) {
//        FeatureCommonVO featureCommonVO = new FeatureCommonVO();
//        BeanUtils.copyProperties(featureCommonDTO, featureCommonVO);
//        return featureCommonVO;
//    }
//}
