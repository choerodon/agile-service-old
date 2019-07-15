//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.FeatureExportVO;
//import io.choerodon.agile.infra.dataobject.FeatureExportDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/4/23.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class FeatureExportConverter implements ConvertorI<Object, FeatureExportDTO, FeatureExportVO> {
//
//    @Override
//    public FeatureExportVO doToDto(FeatureExportDTO featureExportDTO) {
//        FeatureExportVO featureExportVO = new FeatureExportVO();
//        BeanUtils.copyProperties(featureExportDTO, featureExportVO);
//        return featureExportVO;
//    }
//}
