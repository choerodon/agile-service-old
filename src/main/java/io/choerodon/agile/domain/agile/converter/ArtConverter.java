package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.ArtDTO;
import io.choerodon.agile.domain.agile.entity.ArtE;
import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ArtConverter implements ConvertorI<ArtE, ArtDTO, ArtDO> {

    @Override
    public ArtE dtoToEntity(ArtDO artDO) {
        ArtE artE = new ArtE();
        BeanUtils.copyProperties(artDO, artE);
        return artE;
    }

    @Override
    public ArtDO entityToDto(ArtE artE) {
        ArtDO artDO = new ArtDO();
        BeanUtils.copyProperties(artE, artDO);
        return artDO;
    }

    @Override
    public ArtE doToEntity(ArtDTO artDTO) {
        ArtE artE = new ArtE();
        BeanUtils.copyProperties(artDTO, artE);
        return artE;
    }

    @Override
    public ArtDTO entityToDo(ArtE artE) {
        ArtDTO artDTO = new ArtDTO();
        BeanUtils.copyProperties(artE, artDTO);
        return artDTO;
    }

    @Override
    public ArtDO doToDto(ArtDTO artDTO) {
        ArtDO artDO = new ArtDO();
        BeanUtils.copyProperties(artDTO, artDO);
        return artDO;
    }

    @Override
    public ArtDTO dtoToDo(ArtDO artDO) {
        ArtDTO artDTO = new ArtDTO();
        BeanUtils.copyProperties(artDO, artDTO);
        return artDTO;
    }
}
