package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.WikiRelationDTO;
import io.choerodon.agile.app.service.WikiRelationService;
import io.choerodon.agile.domain.agile.entity.WikiRelationE;
import io.choerodon.agile.domain.agile.repository.WikiRelationRepository;
import io.choerodon.agile.infra.dataobject.WikiRelationDO;
import io.choerodon.agile.infra.mapper.WikiRelationMapper;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class WikiRelationServiceImpl implements WikiRelationService {

    @Autowired
    private WikiRelationRepository wikiRelationRepository;

    @Autowired
    private WikiRelationMapper wikiRelationMapper;

    @Override
    public void create(Long projectId, WikiRelationDTO wikiRelationDTO) {
        WikiRelationE wikiRelationE = ConvertHelper.convert(wikiRelationDTO, WikiRelationE.class);
        wikiRelationRepository.create(wikiRelationE);
    }

    @Override
    public List<WikiRelationDTO> queryByIssueId(Long projectId, Long issueId) {
        WikiRelationDO wikiRelationDO = new WikiRelationDO();
        wikiRelationDO.setIssueId(issueId);
        List<WikiRelationDO> wikiRelationDOList = wikiRelationMapper.select(wikiRelationDO);
        return ConvertHelper.convertList(wikiRelationDOList, WikiRelationDTO.class);
    }

    @Override
    public void deleteById(Long projectId, Long id) {
        WikiRelationE wikiRelationE = new WikiRelationE();
        wikiRelationE.setProjectId(projectId);
        wikiRelationE.setId(id);
        wikiRelationRepository.delete(wikiRelationE);
    }
}
