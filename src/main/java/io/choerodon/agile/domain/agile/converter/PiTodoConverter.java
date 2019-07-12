//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.PiTodoVO;
//import io.choerodon.agile.infra.dataobject.PiTodoDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/3/29.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class PiTodoConverter implements ConvertorI<Object, PiTodoDTO, PiTodoVO> {
//
//    @Override
//    public PiTodoVO doToDto(PiTodoDTO piTodoDTO) {
//        PiTodoVO piTodoVO = new PiTodoVO();
//        BeanUtils.copyProperties(piTodoDTO, piTodoVO);
//        return piTodoVO;
//    }
//}
