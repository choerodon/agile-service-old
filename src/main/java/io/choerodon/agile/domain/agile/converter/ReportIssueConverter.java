//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.ReportIssueVO;
//import io.choerodon.agile.infra.dataobject.ReportIssueConvertDTO;
//import io.choerodon.agile.infra.dataobject.ReportIssueDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/6/20
// */
//@Component
//public class ReportIssueConverter implements ConvertorI<ReportIssueConvertDTO, ReportIssueDTO, ReportIssueVO> {
//
//    @Override
//    public ReportIssueConvertDTO dtoToEntity(ReportIssueVO reportIssueVO) {
//        ReportIssueConvertDTO reportIssueConvertDTO = new ReportIssueConvertDTO();
//        BeanUtils.copyProperties(reportIssueVO, reportIssueConvertDTO);
//        return reportIssueConvertDTO;
//    }
//
//    @Override
//    public ReportIssueConvertDTO doToEntity(ReportIssueDTO reportIssueDTO) {
//        ReportIssueConvertDTO reportIssueConvertDTO = new ReportIssueConvertDTO();
//        BeanUtils.copyProperties(reportIssueDTO, reportIssueConvertDTO);
//        return reportIssueConvertDTO;
//    }
//
//    @Override
//    public ReportIssueVO entityToDto(ReportIssueConvertDTO reportIssueConvertDTO) {
//        ReportIssueVO reportIssueVO = new ReportIssueVO();
//        BeanUtils.copyProperties(reportIssueConvertDTO, reportIssueVO);
//        return reportIssueVO;
//    }
//
//    @Override
//    public ReportIssueDTO entityToDo(ReportIssueConvertDTO reportIssueConvertDTO) {
//        ReportIssueDTO reportIssueDTO = new ReportIssueDTO();
//        BeanUtils.copyProperties(reportIssueConvertDTO, reportIssueDTO);
//        return reportIssueDTO;
//    }
//
//    @Override
//    public ReportIssueVO doToDto(ReportIssueDTO reportIssueDTO) {
//        ReportIssueVO reportIssueVO = new ReportIssueVO();
//        BeanUtils.copyProperties(reportIssueDTO, reportIssueVO);
//        return reportIssueVO;
//    }
//
//    @Override
//    public ReportIssueDTO dtoToDo(ReportIssueVO reportIssueVO) {
//        ReportIssueDTO reportIssueDTO = new ReportIssueDTO();
//        BeanUtils.copyProperties(reportIssueVO, reportIssueDTO);
//        return reportIssueDTO;
//    }
//}