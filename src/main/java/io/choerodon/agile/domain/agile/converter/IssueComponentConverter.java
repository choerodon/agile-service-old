//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.agile.api.vo.IssueComponentVO;
//import io.choerodon.agile.domain.agile.entity.IssueComponentE;
//import io.choerodon.agile.infra.dataobject.IssueComponentDTO;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/14.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class IssueComponentConverter implements ConvertorI<IssueComponentE, IssueComponentDTO, IssueComponentVO> {
//
//    @Override
//    public IssueComponentE dtoToEntity(IssueComponentVO issueComponentVO) {
//        IssueComponentE issueComponentE = new IssueComponentE();
//        BeanUtils.copyProperties(issueComponentVO, issueComponentE);
//        return issueComponentE;
//    }
//
//    @Override
//    public IssueComponentVO entityToDto(IssueComponentE issueComponentE) {
//        IssueComponentVO issueComponentVO = new IssueComponentVO();
//        BeanUtils.copyProperties(issueComponentE, issueComponentVO);
//        return issueComponentVO;
//    }
//
//    @Override
//    public IssueComponentE doToEntity(IssueComponentDTO issueComponentDTO) {
//        IssueComponentE issueComponentE = new IssueComponentE();
//        BeanUtils.copyProperties(issueComponentDTO, issueComponentE);
//        return issueComponentE;
//    }
//
//    @Override
//    public IssueComponentDTO entityToDo(IssueComponentE issueComponentE) {
//        IssueComponentDTO issueComponentDTO = new IssueComponentDTO();
//        BeanUtils.copyProperties(issueComponentE, issueComponentDTO);
//        return issueComponentDTO;
//    }
//
//    @Override
//    public IssueComponentVO doToDto(IssueComponentDTO issueComponentDTO) {
//        IssueComponentVO issueComponentVO = new IssueComponentVO();
//        BeanUtils.copyProperties(issueComponentDTO, issueComponentVO);
//        return issueComponentVO;
//    }
//
//    @Override
//    public IssueComponentDTO dtoToDo(IssueComponentVO issueComponentVO) {
//        IssueComponentDTO issueComponentDTO = new IssueComponentDTO();
//        BeanUtils.copyProperties(issueComponentVO, issueComponentDTO);
//        return issueComponentDTO;
//    }
//}
