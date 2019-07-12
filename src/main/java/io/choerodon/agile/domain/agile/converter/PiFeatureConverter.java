//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.domain.agile.entity.PiFeatureE;
//import io.choerodon.agile.infra.dataobject.PiFeatureDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/3/26.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class PiFeatureConverter implements ConvertorI<PiFeatureE, PiFeatureDTO, Object> {
//
//    @Override
//    public PiFeatureE doToEntity(PiFeatureDTO piFeatureDTO) {
//        PiFeatureE piFeatureE = new PiFeatureE();
//        BeanUtils.copyProperties(piFeatureDTO, piFeatureE);
//        return piFeatureE;
//    }
//
//    @Override
//    public PiFeatureDTO entityToDo(PiFeatureE piFeatureE) {
//        PiFeatureDTO piFeatureDTO = new PiFeatureDTO();
//        BeanUtils.copyProperties(piFeatureE, piFeatureDTO);
//        return piFeatureDTO;
//    }
//}
