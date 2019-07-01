package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.WikiRelationDTO;
import io.choerodon.agile.api.dto.WorkSpaceDTO;
import io.choerodon.agile.app.service.WikiRelationService;
import io.choerodon.agile.domain.agile.entity.WikiRelationE;
import io.choerodon.agile.infra.dataobject.WorkSpaceDO;
import io.choerodon.agile.infra.feign.KnowledgebaseClient;
import io.choerodon.agile.infra.repository.WikiRelationRepository;
import io.choerodon.agile.infra.dataobject.WikiRelationDO;
import io.choerodon.agile.infra.mapper.WikiRelationMapper;
import io.choerodon.core.convertor.ConvertHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WikiRelationServiceImpl implements WikiRelationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiRelationServiceImpl.class);

    @Autowired
    private WikiRelationRepository wikiRelationRepository;

    @Autowired
    private WikiRelationMapper wikiRelationMapper;

    @Autowired
    private KnowledgebaseClient knowledgebaseClient;

    private Boolean checkRepeat(WikiRelationE wikiRelationE) {
        WikiRelationDO wikiRelationDO = new WikiRelationDO();
        wikiRelationDO.setProjectId(wikiRelationE.getProjectId());
        wikiRelationDO.setIssueId(wikiRelationE.getIssueId());
        wikiRelationDO.setSpaceId(wikiRelationE.getSpaceId());
        WikiRelationDO res = wikiRelationMapper.selectOne(wikiRelationDO);
        return res != null;
    }

    @Override
    public void create(Long projectId, List<WikiRelationDTO> wikiRelationDTOList) {
        List<WikiRelationE> wikiRelationEList = ConvertHelper.convertList(wikiRelationDTOList, WikiRelationE.class);
        for (WikiRelationE wikiRelationE : wikiRelationEList) {
            if (!checkRepeat(wikiRelationE)) {
                wikiRelationRepository.create(wikiRelationE);
            }
        }
    }

    @Override
    public JSONObject queryByIssueId(Long projectId, Long issueId) {
        JSONObject jsonObject = new JSONObject();
        WikiRelationDO wikiRelationDO = new WikiRelationDO();
        wikiRelationDO.setIssueId(issueId);
        List<WikiRelationDO> wikiRelationDOList = wikiRelationMapper.select(wikiRelationDO);
        List<WikiRelationDTO> result = new ArrayList<>();
        if (wikiRelationDOList != null && !wikiRelationDOList.isEmpty()) {
            List<Long> spaceIds = wikiRelationDOList.stream().map(WikiRelationDO::getSpaceId).collect(Collectors.toList());
            Map<Long, WorkSpaceDTO> workSpaceMap = knowledgebaseClient.querySpaceByIds(projectId, spaceIds).getBody().stream().collect(Collectors.toMap(WorkSpaceDTO::getId, Function.identity()));
            for (WikiRelationDO wikiRelation : wikiRelationDOList) {
                WikiRelationDTO wikiRelationDTO = new WikiRelationDTO();
                BeanUtils.copyProperties(wikiRelation, wikiRelationDTO);
                wikiRelationDTO.setWorkSpaceDTO(workSpaceMap.get(wikiRelationDTO.getSpaceId()));
            }
        }
        jsonObject.put("wikiRelationList", result);
        return jsonObject;
    }

    @Override
    public void deleteById(Long projectId, Long id) {
        WikiRelationE wikiRelationE = new WikiRelationE();
        wikiRelationE.setProjectId(projectId);
        wikiRelationE.setId(id);
        wikiRelationRepository.delete(wikiRelationE);
    }

    @Override
    public void moveWikiRelation() {
        List<WikiRelationDO> wikiRelationDOList = wikiRelationMapper.selectAll();
        if (wikiRelationDOList == null || wikiRelationDOList.isEmpty()) {
            return;
        }
        ResponseEntity<List<WorkSpaceDO>> responseEntity = knowledgebaseClient.queryAllSpaceByProject();
        if (responseEntity == null) {
            return;
        }
        List<WorkSpaceDO> workSpaceDOList = responseEntity.getBody();
        if (workSpaceDOList != null && !workSpaceDOList.isEmpty()) {
            for (WikiRelationDO wikiRelationDO : wikiRelationDOList) {
                for (WorkSpaceDO workSpaceDO : workSpaceDOList) {
                    if (workSpaceDO.getProjectId() != null && wikiRelationDO.getProjectId() != null  && wikiRelationDO.getProjectId().equals(workSpaceDO.getProjectId()) && wikiRelationDO.getWikiName() != null && workSpaceDO.getName() != null && wikiRelationDO.getWikiName().equals(workSpaceDO.getName())) {
                        wikiRelationMapper.updateByOptions(wikiRelationDO.getId(), workSpaceDO.getId());
                        break;
                    }
                }
            }
        }
        LOGGER.info("==================================== Finished to fix wiki relation by agile! =============================================");
    }
}
