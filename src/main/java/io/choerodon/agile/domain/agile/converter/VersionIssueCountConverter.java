//package io.choerodon.agile.domain.agile.converter;
//
//
//import io.choerodon.agile.api.vo.VersionIssueCountVO;
//import io.choerodon.agile.infra.dataobject.ProductVersionStatisticsDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/8/27.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class VersionIssueCountConverter implements ConvertorI<Object, ProductVersionStatisticsDTO, VersionIssueCountVO> {
//
//    @Override
//    public VersionIssueCountVO doToDto(ProductVersionStatisticsDTO productVersionStatisticsDTO) {
//        VersionIssueCountVO versionIssueCountVO = new VersionIssueCountVO();
//        BeanUtils.copyProperties(productVersionStatisticsDTO, versionIssueCountVO);
//        return versionIssueCountVO;
//    }
//}
