//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.WorkLogVO;
//import io.choerodon.agile.domain.agile.entity.WorkLogE;
//import io.choerodon.agile.infra.dataobject.WorkLogDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/18.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class WorkLogConverter implements ConvertorI<WorkLogE, WorkLogDTO, WorkLogVO> {
//
//    @Override
//    public WorkLogE dtoToEntity(WorkLogVO workLogVO) {
//        WorkLogE workLogE = new WorkLogE();
//        BeanUtils.copyProperties(workLogVO, workLogE);
//        return workLogE;
//    }
//
//    @Override
//    public WorkLogVO entityToDto(WorkLogE workLogE) {
//        WorkLogVO workLogVO = new WorkLogVO();
//        BeanUtils.copyProperties(workLogE, workLogVO);
//        return workLogVO;
//    }
//
//    @Override
//    public WorkLogE doToEntity(WorkLogDTO workLogDTO) {
//        WorkLogE workLogE = new WorkLogE();
//        BeanUtils.copyProperties(workLogDTO, workLogE);
//        return workLogE;
//    }
//
//    @Override
//    public WorkLogDTO entityToDo(WorkLogE workLogE) {
//        WorkLogDTO workLogDTO = new WorkLogDTO();
//        BeanUtils.copyProperties(workLogE, workLogDTO);
//        return workLogDTO;
//    }
//
//    @Override
//    public WorkLogVO doToDto(WorkLogDTO workLogDTO) {
//        WorkLogVO workLogVO = new WorkLogVO();
//        BeanUtils.copyProperties(workLogDTO, workLogVO);
//        workLogVO.setUserId(workLogDTO.getCreatedBy());
//        return workLogVO;
//    }
//
//    @Override
//    public WorkLogDTO dtoToDo(WorkLogVO workLogVO) {
//        WorkLogDTO workLogDTO = new WorkLogDTO();
//        BeanUtils.copyProperties(workLogVO, workLogDTO);
//        return workLogDTO;
//    }
//}
