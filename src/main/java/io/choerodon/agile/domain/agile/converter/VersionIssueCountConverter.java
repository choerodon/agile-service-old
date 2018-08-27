package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.dto.VersionIssueCountDTO;
import io.choerodon.agile.infra.dataobject.ProductVersionStatisticsDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/27.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class VersionIssueCountConverter implements ConvertorI<Object, ProductVersionStatisticsDO, VersionIssueCountDTO> {

    @Override
    public VersionIssueCountDTO doToDto(ProductVersionStatisticsDO productVersionStatisticsDO) {
        VersionIssueCountDTO versionIssueCountDTO = new VersionIssueCountDTO();
        BeanUtils.copyProperties(productVersionStatisticsDO, versionIssueCountDTO);
        return versionIssueCountDTO;
    }
}
