package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import io.choerodon.agile.infra.dataobject.ProductVersionDTO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class ProductVersionConverter {
    public ProductVersionDTO entityToDo(ProductVersionE versionE) {
        ProductVersionDTO versionDO = new ProductVersionDTO();
        BeanUtils.copyProperties(versionE, versionDO);
        return versionDO;
    }

    public ProductVersionE doToEntity(ProductVersionDTO versionDO) {
        ProductVersionE versionE = new ProductVersionE();
        BeanUtils.copyProperties(versionDO, versionE);
        return versionE;
    }
}
