//package io.choerodon.agile.domain.agile.converter;
//
//
//import io.choerodon.agile.infra.dataobject.LookupValueDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.BeanUtils;
//import io.choerodon.agile.api.vo.LookupValueVO;
//import io.choerodon.agile.domain.agile.entity.LookupValueE;
//
///**
// * 敏捷开发code键值
// *
// * @author dinghuang123@gmail.com
// * @since 2018-05-15 09:40:27
// */
//@Component
//public class LookupValueConverter implements ConvertorI<LookupValueE, LookupValueDTO, LookupValueVO> {
//
//    @Override
//    public LookupValueE dtoToEntity(LookupValueVO lookupValueVO) {
//        LookupValueE lookupValueE = new LookupValueE();
//        BeanUtils.copyProperties(lookupValueVO, lookupValueE);
//        return lookupValueE;
//    }
//
//    @Override
//    public LookupValueE doToEntity(LookupValueDTO lookupValueDTO) {
//        LookupValueE lookupValueE = new LookupValueE();
//        BeanUtils.copyProperties(lookupValueDTO, lookupValueE);
//        return lookupValueE;
//    }
//
//    @Override
//    public LookupValueVO entityToDto(LookupValueE lookupValueE) {
//        LookupValueVO lookupValueVO = new LookupValueVO();
//        BeanUtils.copyProperties(lookupValueE, lookupValueVO);
//        return lookupValueVO;
//    }
//
//    @Override
//    public LookupValueDTO entityToDo(LookupValueE lookupValueE) {
//        LookupValueDTO lookupValueDTO = new LookupValueDTO();
//        BeanUtils.copyProperties(lookupValueE, lookupValueDTO);
//        return lookupValueDTO;
//    }
//
//    @Override
//    public LookupValueVO doToDto(LookupValueDTO lookupValueDTO) {
//        LookupValueVO lookupValueVO = new LookupValueVO();
//        BeanUtils.copyProperties(lookupValueDTO, lookupValueVO);
//        return lookupValueVO;
//    }
//
//    @Override
//    public LookupValueDTO dtoToDo(LookupValueVO lookupValueVO) {
//        LookupValueDTO lookupValueDTO = new LookupValueDTO();
//        BeanUtils.copyProperties(lookupValueVO, lookupValueDTO);
//        return lookupValueDTO;
//    }
//}