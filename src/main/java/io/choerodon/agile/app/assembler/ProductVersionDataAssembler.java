package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.ProductVersionDataDTO;
import io.choerodon.agile.api.dto.ProductVersionDetailDTO;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import io.choerodon.agile.infra.dataobject.ProductVersionDataDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * reated by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class ProductVersionDataAssembler {
    public ProductVersionDataDTO doToDTO(ProductVersionDataDO versionDataDO) {
        ProductVersionDataDTO versionDataDTO = new ProductVersionDataDTO();
        BeanUtils.copyProperties(versionDataDO, versionDataDTO);
        return versionDataDTO;
    }

    public List<ProductVersionDataDTO> doListToDTO(List<ProductVersionDataDO> versionDataDOList) {
        if (versionDataDOList == null) {
            return new ArrayList<>();
        }
        List<ProductVersionDataDTO> versionDataDTOS = new ArrayList<>();
        versionDataDOList.forEach(versionData -> versionDataDTOS.add(doToDTO(versionData)));
        return versionDataDTOS;
    }

    public ProductVersionDetailDTO doToVersionDetailDTO(ProductVersionDO productVersionDO){
        ProductVersionDetailDTO productVersionDetail = new ProductVersionDetailDTO();
        BeanUtils.copyProperties(productVersionDO, productVersionDetail);
        return productVersionDetail;
    }
}
