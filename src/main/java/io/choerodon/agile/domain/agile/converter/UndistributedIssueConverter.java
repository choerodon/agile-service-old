package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.UndistributedIssueVO;
import io.choerodon.agile.infra.dataobject.UndistributedIssueDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/28.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class UndistributedIssueConverter implements ConvertorI<Object, UndistributedIssueDTO, UndistributedIssueVO> {

    @Override
    public UndistributedIssueVO doToDto(UndistributedIssueDTO undistributedIssueDTO) {
        UndistributedIssueVO undistributedIssueVO = new UndistributedIssueVO();
        BeanUtils.copyProperties(undistributedIssueDTO, undistributedIssueVO);
        return undistributedIssueVO;
    }
}
