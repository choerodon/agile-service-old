//package io.choerodon.agile.domain.agile.converter;
//
//
//import io.choerodon.agile.api.vo.ActiveSprintVO;
//import io.choerodon.agile.infra.dataobject.SprintDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/8/27.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class ActiveSprintConverter implements ConvertorI<Object, SprintDTO, ActiveSprintVO> {
//
//    @Override
//    public ActiveSprintVO doToDto(SprintDTO sprintDTO) {
//        ActiveSprintVO activeSprintVO = new ActiveSprintVO();
//        BeanUtils.copyProperties(sprintDTO, activeSprintVO);
//        return activeSprintVO;
//    }
//}
