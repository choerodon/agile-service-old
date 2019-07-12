//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.ProjectInfoVO;
//import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
//import io.choerodon.agile.infra.dataobject.ProjectInfoDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/6/15
// */
//@Component
//public class ProjectInfoConverter implements ConvertorI<ProjectInfoE, ProjectInfoDTO, ProjectInfoVO> {
//
//    @Override
//    public ProjectInfoE dtoToEntity(ProjectInfoVO projectInfoVO) {
//        ProjectInfoE projectInfoE = new ProjectInfoE();
//        BeanUtils.copyProperties(projectInfoVO, projectInfoE);
//        return projectInfoE;
//    }
//
//    @Override
//    public ProjectInfoE doToEntity(ProjectInfoDTO projectInfoDTO) {
//        ProjectInfoE projectInfoE = new ProjectInfoE();
//        BeanUtils.copyProperties(projectInfoDTO, projectInfoE);
//        return projectInfoE;
//    }
//
//    @Override
//    public ProjectInfoVO entityToDto(ProjectInfoE projectInfoE) {
//        ProjectInfoVO projectInfoVO = new ProjectInfoVO();
//        BeanUtils.copyProperties(projectInfoE, projectInfoVO);
//        return projectInfoVO;
//    }
//
//    @Override
//    public ProjectInfoDTO entityToDo(ProjectInfoE projectInfoE) {
//        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
//        BeanUtils.copyProperties(projectInfoE, projectInfoDTO);
//        return projectInfoDTO;
//    }
//
//    @Override
//    public ProjectInfoVO doToDto(ProjectInfoDTO projectInfoDTO) {
//        ProjectInfoVO projectInfoVO = new ProjectInfoVO();
//        BeanUtils.copyProperties(projectInfoDTO, projectInfoVO);
//        return projectInfoVO;
//    }
//
//    @Override
//    public ProjectInfoDTO dtoToDo(ProjectInfoVO projectInfoVO) {
//        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
//        BeanUtils.copyProperties(projectInfoVO, projectInfoDTO);
//        return projectInfoDTO;
//    }
//}
