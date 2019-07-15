//package io.choerodon.agile.domain.agile.converter;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//import io.choerodon.agile.api.vo.IssueTypeDistributeVO;
//import io.choerodon.agile.infra.dataobject.IssueTypeDistributeDTO;
//import io.choerodon.core.convertor.ConvertorI;
//
///**
// * Creator: ChangpingShi0213@gmail.com
// * Date:  16:31 2018/9/4
// * Description:
// */
//@Component
//public class IssueTypeDistributeConverter implements ConvertorI<Object, IssueTypeDistributeDTO, IssueTypeDistributeVO> {
//
//    @Override
//    public IssueTypeDistributeVO doToDto(IssueTypeDistributeDTO issueTypeDistributeDTO) {
//        IssueTypeDistributeVO issueTypeDistributeVO = new IssueTypeDistributeVO();
//        BeanUtils.copyProperties(issueTypeDistributeDTO, issueTypeDistributeVO);
//        return issueTypeDistributeVO;
//    }
//}