package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.agile.infra.dataobject.ProductVersionCommonDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/20.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class VersionCommonConverter implements ConvertorI<Object, ProductVersionCommonDO, ProductVersionDTO> {

    @Override
    public ProductVersionDTO doToDto(ProductVersionCommonDO productVersionCommonDO) {
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        BeanUtils.copyProperties(productVersionCommonDO, productVersionDTO);
        return productVersionDTO;
    }

    @Override
    public ProductVersionCommonDO dtoToDo(ProductVersionDTO productVersionDTO) {
        ProductVersionCommonDO productVersionCommonDO = new ProductVersionCommonDO();
        BeanUtils.copyProperties(productVersionDTO, productVersionCommonDO);
        return productVersionCommonDO;
    }
}
