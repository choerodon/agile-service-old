package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Component
public class ProductVersionPageAssembler {

    public ProductVersionPageDTO doToDto(ProductVersionDO versionDO){
        ProductVersionPageDTO versionPageDTO = new ProductVersionPageDTO();
        BeanUtils.copyProperties(versionDO, versionPageDTO);
        return versionPageDTO;
    }

    public List<ProductVersionPageDTO> doListToDto(List<ProductVersionDO> versionDOS){
        List<ProductVersionPageDTO> versionPageDTOList = new ArrayList<>();
        versionDOS.forEach(versionDO -> versionPageDTOList.add(doToDto(versionDO)));
        return versionPageDTOList;
    }
}
