package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.ArtVO;
import io.choerodon.agile.domain.agile.entity.ArtE;
import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ArtConverter implements ConvertorI<ArtE, ArtVO, ArtDTO> {

    @Override
    public ArtE dtoToEntity(ArtDTO artDTO) {
        ArtE artE = new ArtE();
        BeanUtils.copyProperties(artDTO, artE);
        return artE;
    }

    @Override
    public ArtDTO entityToDto(ArtE artE) {
        ArtDTO artDTO = new ArtDTO();
        BeanUtils.copyProperties(artE, artDTO);
        return artDTO;
    }

    @Override
    public ArtE doToEntity(ArtVO artVO) {
        ArtE artE = new ArtE();
        BeanUtils.copyProperties(artVO, artE);
        return artE;
    }

    @Override
    public ArtVO entityToDo(ArtE artE) {
        ArtVO artVO = new ArtVO();
        BeanUtils.copyProperties(artE, artVO);
        return artVO;
    }

    @Override
    public ArtDTO doToDto(ArtVO artVO) {
        ArtDTO artDTO = new ArtDTO();
        BeanUtils.copyProperties(artVO, artDTO);
        return artDTO;
    }

    @Override
    public ArtVO dtoToDo(ArtDTO artDTO) {
        ArtVO artVO = new ArtVO();
        BeanUtils.copyProperties(artDTO, artVO);
        return artVO;
    }
}
