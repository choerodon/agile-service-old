package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IssueCountDTO;
import io.choerodon.agile.api.dto.ProductVersionNameDTO;
import io.choerodon.agile.api.dto.ProductVersionStatisticsDTO;
import io.choerodon.agile.infra.dataobject.IssueCountDO;
import io.choerodon.agile.infra.dataobject.ProductVersionNameDO;
import io.choerodon.agile.infra.dataobject.ProductVersionStatisticsDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/18.
 */
@Component
public class ProductVersionStatisticsAssembler {
    public ProductVersionStatisticsDTO doToDto(ProductVersionStatisticsDO versionStatisticsDO){
        ProductVersionStatisticsDTO versionStatisticsDTO = new ProductVersionStatisticsDTO();
        BeanUtils.copyProperties(versionStatisticsDO, versionStatisticsDTO);
        return versionStatisticsDTO;
    }

    public List<IssueCountDTO> doListToIssueCountDto(List<IssueCountDO> issueCountDOList){
        List<IssueCountDTO> issueCountDTOList = new ArrayList<>();
        issueCountDOList.forEach(issueCountDO -> {
            IssueCountDTO issueCountDTO = new IssueCountDTO();
            BeanUtils.copyProperties(issueCountDO, issueCountDTO);
            issueCountDTOList.add(issueCountDTO);
        });
        return issueCountDTOList;
    }

    public List<ProductVersionNameDTO> doListToVersionNameDto(List<ProductVersionNameDO> productVersionNames){
        List<ProductVersionNameDTO> productVersionNameDTOList = new ArrayList<>();
        productVersionNames.forEach(productVersionName -> {
            ProductVersionNameDTO productVersionNameDTO = new ProductVersionNameDTO();
            BeanUtils.copyProperties(productVersionName, productVersionNameDTO);
            productVersionNameDTOList.add(productVersionNameDTO);
        });
        return productVersionNameDTOList;
    }
}
