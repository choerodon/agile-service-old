//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.PiVO;
//import io.choerodon.agile.domain.agile.entity.PiE;
//import io.choerodon.agile.infra.dataobject.PiDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2019/3/11.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class PiConverter implements ConvertorI<PiE, PiVO, PiDTO> {
//
//    @Override
//    public PiE dtoToEntity(PiDTO piDTO) {
//        PiE piE = new PiE();
//        BeanUtils.copyProperties(piDTO, piE);
//        return piE;
//    }
//
//    @Override
//    public PiDTO entityToDto(PiE piE) {
//        PiDTO piDTO = new PiDTO();
//        BeanUtils.copyProperties(piE, piDTO);
//        return piDTO;
//    }
//
//    @Override
//    public PiE doToEntity(PiVO piVO) {
//        PiE piE = new PiE();
//        BeanUtils.copyProperties(piVO, piE);
//        return piE;
//    }
//
//    @Override
//    public PiVO entityToDo(PiE piE) {
//        PiVO piVO = new PiVO();
//        BeanUtils.copyProperties(piE, piVO);
//        return piVO;
//    }
//
//    @Override
//    public PiDTO doToDto(PiVO piVO) {
//        PiDTO piDTO = new PiDTO();
//        BeanUtils.copyProperties(piVO, piDTO);
//        return piDTO;
//    }
//
//    @Override
//    public PiVO dtoToDo(PiDTO piDTO) {
//        PiVO piVO = new PiVO();
//        BeanUtils.copyProperties(piDTO, piVO);
//        return piVO;
//    }
//}
