package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class ProductVersionConverter {
    public ProductVersionDO entityToDo(ProductVersionE versionE) {
        ProductVersionDO versionDO = new ProductVersionDO();
        BeanUtils.copyProperties(versionE, versionDO);
        return versionDO;
    }

    public ProductVersionE doToEntity(ProductVersionDO versionDO) {
        ProductVersionE versionE = new ProductVersionE();
        BeanUtils.copyProperties(versionDO, versionE);
        return versionE;
    }
}
