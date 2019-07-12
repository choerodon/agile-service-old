//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.ComponentForListVO;
//import io.choerodon.agile.infra.dataobject.ComponentForListDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/31.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class ComponentForListConverter implements ConvertorI<Object, ComponentForListDTO, ComponentForListVO> {
//
//    @Override
//    public ComponentForListVO doToDto(ComponentForListDTO componentForListDTO) {
//        ComponentForListVO componentForListVO = new ComponentForListVO();
//        BeanUtils.copyProperties(componentForListDTO, componentForListVO);
//        return componentForListVO;
//    }
//
//    @Override
//    public ComponentForListDTO dtoToDo(ComponentForListVO componentForListVO) {
//        ComponentForListDTO componentForListDTO = new ComponentForListDTO();
//        BeanUtils.copyProperties(componentForListVO, componentForListDTO);
//        return componentForListDTO;
//    }
//}
