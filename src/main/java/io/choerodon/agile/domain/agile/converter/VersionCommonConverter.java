//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.ProductVersionVO;
//import io.choerodon.agile.infra.dataobject.ProductVersionCommonDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/6/20.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class VersionCommonConverter implements ConvertorI<Object, ProductVersionCommonDTO, ProductVersionVO> {
//
//    @Override
//    public ProductVersionVO doToDto(ProductVersionCommonDTO productVersionCommonDTO) {
//        ProductVersionVO productVersionVO = new ProductVersionVO();
//        BeanUtils.copyProperties(productVersionCommonDTO, productVersionVO);
//        return productVersionVO;
//    }
//
//    @Override
//    public ProductVersionCommonDTO dtoToDo(ProductVersionVO productVersionVO) {
//        ProductVersionCommonDTO productVersionCommonDTO = new ProductVersionCommonDTO();
//        BeanUtils.copyProperties(productVersionVO, productVersionCommonDTO);
//        return productVersionCommonDTO;
//    }
//}
