//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.QuickFilterVO;
//import io.choerodon.agile.domain.agile.entity.QuickFilterE;
//import io.choerodon.agile.infra.dataobject.QuickFilterDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/6/13.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class QuickFilterConverter implements ConvertorI<QuickFilterE, QuickFilterDTO, QuickFilterVO> {
//
//    @Override
//    public QuickFilterE dtoToEntity(QuickFilterVO quickFilterVO) {
//        QuickFilterE quickFilterE = new QuickFilterE();
//        BeanUtils.copyProperties(quickFilterVO, quickFilterE);
//        return quickFilterE;
//    }
//
//    @Override
//    public QuickFilterVO entityToDto(QuickFilterE quickFilterE) {
//        QuickFilterVO quickFilterVO = new QuickFilterVO();
//        BeanUtils.copyProperties(quickFilterE, quickFilterVO);
//        return quickFilterVO;
//    }
//
//    @Override
//    public QuickFilterE doToEntity(QuickFilterDTO quickFilterDTO) {
//        QuickFilterE quickFilterE = new QuickFilterE();
//        BeanUtils.copyProperties(quickFilterDTO, quickFilterE);
//        return quickFilterE;
//    }
//
//    @Override
//    public QuickFilterDTO entityToDo(QuickFilterE quickFilterE) {
//        QuickFilterDTO quickFilterDTO = new QuickFilterDTO();
//        BeanUtils.copyProperties(quickFilterE, quickFilterDTO);
//        return quickFilterDTO;
//    }
//
//    @Override
//    public QuickFilterVO doToDto(QuickFilterDTO quickFilterDTO) {
//        QuickFilterVO quickFilterVO = new QuickFilterVO();
//        BeanUtils.copyProperties(quickFilterDTO, quickFilterVO);
//        return quickFilterVO;
//    }
//
//    @Override
//    public QuickFilterDTO dtoToDo(QuickFilterVO quickFilterVO) {
//        QuickFilterDTO quickFilterDTO = new QuickFilterDTO();
//        BeanUtils.copyProperties(quickFilterVO, quickFilterDTO);
//        return quickFilterDTO;
//    }
//}
