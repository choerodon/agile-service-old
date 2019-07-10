package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.WikiRelationVO;
import io.choerodon.agile.domain.agile.entity.WikiRelationE;
import io.choerodon.agile.infra.dataobject.WikiRelationDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class WikiRelationConverter implements ConvertorI<WikiRelationE, WikiRelationDTO, WikiRelationVO> {

    @Override
    public WikiRelationE dtoToEntity(WikiRelationVO wikiRelationVO) {
        WikiRelationE wikiRelationE = new WikiRelationE();
        BeanUtils.copyProperties(wikiRelationVO, wikiRelationE);
        return wikiRelationE;
    }

    @Override
    public WikiRelationVO entityToDto(WikiRelationE wikiRelationE) {
        WikiRelationVO wikiRelationVO = new WikiRelationVO();
        BeanUtils.copyProperties(wikiRelationE, wikiRelationVO);
        return wikiRelationVO;
    }

    @Override
    public WikiRelationE doToEntity(WikiRelationDTO wikiRelationDTO) {
        WikiRelationE wikiRelationE = new WikiRelationE();
        BeanUtils.copyProperties(wikiRelationDTO, wikiRelationE);
        return wikiRelationE;
    }

    @Override
    public WikiRelationDTO entityToDo(WikiRelationE wikiRelationE) {
        WikiRelationDTO wikiRelationDTO = new WikiRelationDTO();
        BeanUtils.copyProperties(wikiRelationE, wikiRelationDTO);
        return wikiRelationDTO;
    }

    @Override
    public WikiRelationVO doToDto(WikiRelationDTO wikiRelationDTO) {
        WikiRelationVO wikiRelationVO = new WikiRelationVO();
        BeanUtils.copyProperties(wikiRelationDTO, wikiRelationVO);
        return wikiRelationVO;
    }

    @Override
    public WikiRelationDTO dtoToDo(WikiRelationVO wikiRelationVO) {
        WikiRelationDTO wikiRelationDTO = new WikiRelationDTO();
        BeanUtils.copyProperties(wikiRelationVO, wikiRelationDTO);
        return wikiRelationDTO;
    }
}
