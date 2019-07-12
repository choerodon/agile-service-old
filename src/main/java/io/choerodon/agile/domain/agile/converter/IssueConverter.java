//package io.choerodon.agile.domain.agile.converter;
//
//
//import io.choerodon.agile.api.vo.IssueVO;
//import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
//import io.choerodon.agile.infra.dataobject.IssueDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.BeanUtils;
//
///**
// * 敏捷开发Issue
// *
// * @author dinghuang123@gmail.com
// * @since 2018-05-14 20:30:48
// */
//@Component
//public class IssueConverter implements ConvertorI<IssueConvertDTO, IssueDTO, IssueVO> {
//
//    @Override
//    public IssueConvertDTO dtoToEntity(IssueVO issueVO) {
//        IssueConvertDTO issueConvertDTO = new IssueConvertDTO();
//        BeanUtils.copyProperties(issueVO, issueConvertDTO);
//        return issueConvertDTO;
//    }
//
//    @Override
//    public IssueConvertDTO doToEntity(IssueDTO issueDTO) {
//        IssueConvertDTO issueConvertDTO = new IssueConvertDTO();
//        BeanUtils.copyProperties(issueDTO, issueConvertDTO);
//        return issueConvertDTO;
//    }
//
//    @Override
//    public IssueVO entityToDto(IssueConvertDTO issueConvertDTO) {
//        IssueVO issueVO = new IssueVO();
//        BeanUtils.copyProperties(issueConvertDTO, issueVO);
//        return issueVO;
//    }
//
//    @Override
//    public IssueDTO entityToDo(IssueConvertDTO issueConvertDTO) {
//        IssueDTO issueDTO = new IssueDTO();
//        BeanUtils.copyProperties(issueConvertDTO, issueDTO);
//        return issueDTO;
//    }
//
//    @Override
//    public IssueVO doToDto(IssueDTO issueDTO) {
//        IssueVO issueVO = new IssueVO();
//        BeanUtils.copyProperties(issueDTO, issueVO);
//        return issueVO;
//    }
//
//    @Override
//    public IssueDTO dtoToDo(IssueVO issueVO) {
//        IssueDTO issueDTO = new IssueDTO();
//        BeanUtils.copyProperties(issueVO, issueDTO);
//        return issueDTO;
//    }
//
//}