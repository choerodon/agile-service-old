package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.UndistributedIssueDTO;
import io.choerodon.agile.infra.dataobject.UndistributedIssueDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/28.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class UndistributedIssueConverter implements ConvertorI<Object, UndistributedIssueDO, UndistributedIssueDTO> {

    @Override
    public UndistributedIssueDTO doToDto(UndistributedIssueDO undistributedIssueDO) {
        UndistributedIssueDTO undistributedIssueDTO = new UndistributedIssueDTO();
        BeanUtils.copyProperties(undistributedIssueDO, undistributedIssueDTO);
        return undistributedIssueDTO;
    }
}
