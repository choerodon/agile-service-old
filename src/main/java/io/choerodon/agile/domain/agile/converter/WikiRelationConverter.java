package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.WikiRelationDTO;
import io.choerodon.agile.domain.agile.entity.WikiRelationE;
import io.choerodon.agile.infra.dataobject.WikiRelationDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class WikiRelationConverter implements ConvertorI<WikiRelationE, WikiRelationDO, WikiRelationDTO> {

    @Override
    public WikiRelationE dtoToEntity(WikiRelationDTO wikiRelationDTO) {
        WikiRelationE wikiRelationE = new WikiRelationE();
        BeanUtils.copyProperties(wikiRelationDTO, wikiRelationE);
        return wikiRelationE;
    }

    @Override
    public WikiRelationDTO entityToDto(WikiRelationE wikiRelationE) {
        WikiRelationDTO wikiRelationDTO = new WikiRelationDTO();
        BeanUtils.copyProperties(wikiRelationE, wikiRelationDTO);
        return wikiRelationDTO;
    }

    @Override
    public WikiRelationE doToEntity(WikiRelationDO wikiRelationDO) {
        WikiRelationE wikiRelationE = new WikiRelationE();
        BeanUtils.copyProperties(wikiRelationDO, wikiRelationE);
        return wikiRelationE;
    }

    @Override
    public WikiRelationDO entityToDo(WikiRelationE wikiRelationE) {
        WikiRelationDO wikiRelationDO = new WikiRelationDO();
        BeanUtils.copyProperties(wikiRelationE, wikiRelationDO);
        return wikiRelationDO;
    }

    @Override
    public WikiRelationDTO doToDto(WikiRelationDO wikiRelationDO) {
        WikiRelationDTO wikiRelationDTO = new WikiRelationDTO();
        BeanUtils.copyProperties(wikiRelationDO, wikiRelationDTO);
        return wikiRelationDTO;
    }

    @Override
    public WikiRelationDO dtoToDo(WikiRelationDTO wikiRelationDTO) {
        WikiRelationDO wikiRelationDO = new WikiRelationDO();
        BeanUtils.copyProperties(wikiRelationDTO, wikiRelationDO);
        return wikiRelationDO;
    }
}
