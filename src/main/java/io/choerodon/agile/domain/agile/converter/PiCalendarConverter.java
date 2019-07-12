//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.PiCalendarVO;
//import io.choerodon.agile.infra.dataobject.PiCalendarDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/4/25.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class PiCalendarConverter implements ConvertorI<Object, PiCalendarDTO, PiCalendarVO> {
//
//    @Override
//    public PiCalendarVO doToDto(PiCalendarDTO piCalendarDTO) {
//        PiCalendarVO piCalendarVO = new PiCalendarVO();
//        BeanUtils.copyProperties(piCalendarDTO, piCalendarVO);
//        return piCalendarVO;
//    }
//}
