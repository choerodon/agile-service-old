package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.ProductVersionDetailDTO;
import io.choerodon.agile.api.dto.ProductVersionUpdateDTO;
import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class ProductVersionUpdateAssembler {
    public ProductVersionE dtoToEntity(ProductVersionUpdateDTO versionUpdateDTO){
        ProductVersionE versionE = new ProductVersionE();
        BeanUtils.copyProperties(versionUpdateDTO, versionE);
        return versionE;
    }

    public ProductVersionDetailDTO entityToDto(ProductVersionE versionE) {
        ProductVersionDetailDTO versionDetailDTO = new ProductVersionDetailDTO();
        BeanUtils.copyProperties(versionE, versionDetailDTO);
        return versionDetailDTO;
    }
}
