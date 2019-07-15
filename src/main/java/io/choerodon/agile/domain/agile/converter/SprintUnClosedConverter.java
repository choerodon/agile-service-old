//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.SprintUnClosedVO;
//import io.choerodon.agile.infra.dataobject.SprintDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SprintUnClosedConverter implements ConvertorI<Object, SprintDTO, SprintUnClosedVO> {
//
//    @Override
//    public SprintUnClosedVO doToDto(SprintDTO sprintDTO) {
//        SprintUnClosedVO sprintUnClosedVO = new SprintUnClosedVO();
//        BeanUtils.copyProperties(sprintDTO, sprintUnClosedVO);
//        return sprintUnClosedVO;
//    }
//
//}
