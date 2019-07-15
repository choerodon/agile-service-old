//package io.choerodon.agile.domain.agile.converter;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//import io.choerodon.agile.api.vo.AssigneeDistributeVO;
//import io.choerodon.agile.infra.dataobject.AssigneeDistributeDTO;
//import io.choerodon.core.convertor.ConvertorI;
//
///**
// * Creator: ChangpingShi0213@gmail.com
// * Date:  13:51 2018/9/4
// * Description:
// */
//@Component
//public class AssigneeDistributeConverter implements ConvertorI<Object, AssigneeDistributeDTO, AssigneeDistributeVO> {
//
//    @Override
//    public AssigneeDistributeVO doToDto(AssigneeDistributeDTO assigneeDistributeDTO) {
//        AssigneeDistributeVO assigneeDistributeVO = new AssigneeDistributeVO();
//        BeanUtils.copyProperties(assigneeDistributeDTO, assigneeDistributeVO);
//        return assigneeDistributeVO;
//    }
//}
