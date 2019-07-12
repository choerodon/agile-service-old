//package io.choerodon.agile.domain.agile.converter;
//
//
//import io.choerodon.agile.api.vo.ComponentIssueRelVO;
//import io.choerodon.agile.infra.dataobject.ComponentIssueRelDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.BeanUtils;
//import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018-05-15 16:47:27
// */
//@Component
//public class ComponentIssueRelConverter implements ConvertorI<ComponentIssueRelE, ComponentIssueRelDTO, ComponentIssueRelVO> {
//
//    @Override
//    public ComponentIssueRelE dtoToEntity(ComponentIssueRelVO componentIssueRelVO) {
//        ComponentIssueRelE componentIssueRelE = new ComponentIssueRelE();
//        BeanUtils.copyProperties(componentIssueRelVO, componentIssueRelE);
//        return componentIssueRelE;
//    }
//
//    @Override
//    public ComponentIssueRelE doToEntity(ComponentIssueRelDTO componentIssueRelDTO) {
//        ComponentIssueRelE componentIssueRelE = new ComponentIssueRelE();
//        BeanUtils.copyProperties(componentIssueRelDTO, componentIssueRelE);
//        return componentIssueRelE;
//    }
//
//    @Override
//    public ComponentIssueRelVO entityToDto(ComponentIssueRelE componentIssueRelE) {
//        ComponentIssueRelVO componentIssueRelVO = new ComponentIssueRelVO();
//        BeanUtils.copyProperties(componentIssueRelE, componentIssueRelVO);
//        return componentIssueRelVO;
//    }
//
//    @Override
//    public ComponentIssueRelDTO entityToDo(ComponentIssueRelE componentIssueRelE) {
//        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
//        BeanUtils.copyProperties(componentIssueRelE, componentIssueRelDTO);
//        return componentIssueRelDTO;
//    }
//
//    @Override
//    public ComponentIssueRelVO doToDto(ComponentIssueRelDTO componentIssueRelDTO) {
//        ComponentIssueRelVO componentIssueRelVO = new ComponentIssueRelVO();
//        BeanUtils.copyProperties(componentIssueRelDTO, componentIssueRelVO);
//        return componentIssueRelVO;
//    }
//
//    @Override
//    public ComponentIssueRelDTO dtoToDo(ComponentIssueRelVO componentIssueRelVO) {
//        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
//        BeanUtils.copyProperties(componentIssueRelVO, componentIssueRelDTO);
//        return componentIssueRelDTO;
//    }
//}