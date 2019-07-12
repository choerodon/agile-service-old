//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.IssueInfoVO;
//import io.choerodon.agile.infra.dataobject.IssueInfoDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/7/11.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class IssueInfoConverter implements ConvertorI<Object, IssueInfoDTO, IssueInfoVO> {
//
//    @Override
//    public IssueInfoVO doToDto(IssueInfoDTO issueInfoDTO) {
//        IssueInfoVO issueInfoVO = new IssueInfoVO();
//        BeanUtils.copyProperties(issueInfoDTO, issueInfoVO);
//        return issueInfoVO;
//    }
//
//}
