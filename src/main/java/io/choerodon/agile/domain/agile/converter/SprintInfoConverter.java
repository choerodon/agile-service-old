//package io.choerodon.agile.domain.agile.converter;
//
//
//import io.choerodon.agile.api.vo.SprintInfoVO;
//import io.choerodon.agile.infra.dataobject.SprintDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/9/4.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class SprintInfoConverter implements ConvertorI<Object, SprintDTO, SprintInfoVO> {
//
//    @Override
//    public SprintInfoVO doToDto(SprintDTO sprintDTO) {
//        SprintInfoVO sprintInfoVO = new SprintInfoVO();
//        BeanUtils.copyProperties(sprintDTO, sprintInfoVO);
//        return sprintInfoVO;
//    }
//}
