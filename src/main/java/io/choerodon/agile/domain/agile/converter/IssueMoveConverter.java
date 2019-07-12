//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.IssueMoveVO;
//import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/16.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class IssueMoveConverter implements ConvertorI<IssueConvertDTO, Object, IssueMoveVO> {
//
//    @Override
//    public IssueConvertDTO dtoToEntity(IssueMoveVO issueMoveVO) {
//        IssueConvertDTO issueConvertDTO = new IssueConvertDTO();
//        BeanUtils.copyProperties(issueMoveVO, issueConvertDTO);
//        return issueConvertDTO;
//    }
//
//    @Override
//    public IssueMoveVO entityToDto(IssueConvertDTO issueConvertDTO) {
//        IssueMoveVO issueMoveVO = new IssueMoveVO();
//        BeanUtils.copyProperties(issueConvertDTO, issueMoveVO);
//        return issueMoveVO;
//    }
//
//}
