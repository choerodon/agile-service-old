//package io.choerodon.agile.domain.agile.converter;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//import io.choerodon.agile.api.vo.UnfinishedIssueVO;
//import io.choerodon.agile.infra.dataobject.UnfinishedIssueDTO;
//import io.choerodon.core.convertor.ConvertorI;
//
///**
// * Creator: changpingshi0213@gmail.com
// * Date:  17:24 2018/8/28
// * Description:
// */
//@Component
//public class UnfinishedIssueConverter implements ConvertorI<Object, UnfinishedIssueDTO, UnfinishedIssueVO> {
//
//    @Override
//    public UnfinishedIssueVO doToDto(UnfinishedIssueDTO unfinishedIssueDTO) {
//        UnfinishedIssueVO unfinishedIssueVO = new UnfinishedIssueVO();
//        BeanUtils.copyProperties(unfinishedIssueDTO, unfinishedIssueVO);
//        return unfinishedIssueVO;
//    }
//}
