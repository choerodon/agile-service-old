package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.ProductVersionCreateDTO;
import io.choerodon.agile.api.dto.ProductVersionDetailDTO;
import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class ProductVersionCreateAssembler {
    public ProductVersionE dtoToEntity(ProductVersionCreateDTO versionCreateDTO){
        ProductVersionE versionE = new ProductVersionE();
        BeanUtils.copyProperties(versionCreateDTO, versionE);
        return versionE;
    }

    public ProductVersionDetailDTO entityToDto(ProductVersionE versionE) {
        ProductVersionDetailDTO versionDetailDTO = new ProductVersionDetailDTO();
        BeanUtils.copyProperties(versionE, versionDetailDTO);
        return versionDetailDTO;
    }

    public ProductVersionE doToEntity(ProductVersionDO versionDO) {
        ProductVersionE versionE = new ProductVersionE();
        BeanUtils.copyProperties(versionDO, versionE);
        return versionE;
    }
}
