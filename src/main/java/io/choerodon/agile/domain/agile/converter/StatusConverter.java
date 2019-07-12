//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.StatusVO;
//import io.choerodon.agile.infra.dataobject.StatusDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/6/26.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class StatusConverter implements ConvertorI<Object, StatusDTO, StatusVO> {
//
//    @Override
//    public StatusVO doToDto(StatusDTO statusDTO) {
//        StatusVO statusVO = new StatusVO();
//        BeanUtils.copyProperties(statusDTO, statusVO);
//        return statusVO;
//    }
//}
