package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.WikiRelationVO;
import io.choerodon.agile.api.vo.WorkSpaceVO;
import io.choerodon.agile.app.service.WikiRelationService;
import io.choerodon.agile.infra.dataobject.WikiRelationDTO;
import io.choerodon.agile.infra.dataobject.WorkSpaceDTO;
import io.choerodon.agile.infra.feign.KnowledgebaseClient;
import io.choerodon.agile.infra.mapper.WikiRelationMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
    private WikiRelationMapper wikiRelationMapper;

    @Autowired
    private KnowledgebaseClient knowledgebaseClient;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    private Boolean checkRepeat(WikiRelationDTO wikiRelationDTO) {
        WikiRelationDTO wikiRelation = new WikiRelationDTO();
        wikiRelation.setProjectId(wikiRelationDTO.getProjectId());
        wikiRelation.setIssueId(wikiRelationDTO.getIssueId());
        wikiRelation.setSpaceId(wikiRelationDTO.getSpaceId());
        WikiRelationDTO res = wikiRelationMapper.selectOne(wikiRelation);
        return res != null;
    }

    @Override
    public void create(Long projectId, List<WikiRelationVO> wikiRelationVOList) {
        List<WikiRelationDTO> wikiRelationDTOList = modelMapper.map(wikiRelationVOList, new TypeToken<WikiRelationDTO>(){}.getType());
        for (WikiRelationDTO wikiRelationDTO : wikiRelationDTOList) {
            if (!checkRepeat(wikiRelationDTO)) {
                if (wikiRelationMapper.insert(wikiRelationDTO) != 1) {
                    throw new CommonException("error.wikiRelationDTO.insert");
                }
            }
        }
    }

    @Override
    public JSONObject queryByIssueId(Long projectId, Long issueId) {
        JSONObject jsonObject = new JSONObject();
        WikiRelationDTO wikiRelationDTO = new WikiRelationDTO();
        wikiRelationDTO.setIssueId(issueId);
        List<WikiRelationDTO> wikiRelationDTOList = wikiRelationMapper.select(wikiRelationDTO);
        List<WikiRelationVO> result = new ArrayList<>();
        if (wikiRelationDTOList != null && !wikiRelationDTOList.isEmpty()) {
            List<Long> spaceIds = wikiRelationDTOList.stream().map(WikiRelationDTO::getSpaceId).collect(Collectors.toList());
            Map<Long, WorkSpaceVO> workSpaceMap = knowledgebaseClient.querySpaceByIds(projectId, spaceIds).getBody().stream().collect(Collectors.toMap(WorkSpaceVO::getId, Function.identity()));
            for (WikiRelationDTO wikiRelation : wikiRelationDTOList) {
                WikiRelationVO wikiRelationVO = new WikiRelationVO();
                BeanUtils.copyProperties(wikiRelation, wikiRelationVO);
                wikiRelationVO.setWorkSpaceVO(workSpaceMap.get(wikiRelationVO.getSpaceId()));
                result.add(wikiRelationVO);
            }
        }
        jsonObject.put("knowledgeRelationList", result);
        return jsonObject;
    }

    @Override
    public void deleteById(Long projectId, Long id) {
        WikiRelationDTO wikiRelationDTO = new WikiRelationDTO();
        wikiRelationDTO.setProjectId(projectId);
        wikiRelationDTO.setId(id);
        if (wikiRelationMapper.delete(wikiRelationDTO) != 1) {
            throw new CommonException("error.wikiRelationDTO.delete");
        }
    }

    @Override
    public void moveWikiRelation() {
        List<WikiRelationDTO> wikiRelationDTOList = wikiRelationMapper.selectAll();
        if (wikiRelationDTOList == null || wikiRelationDTOList.isEmpty()) {
            return;
        }
        ResponseEntity<List<WorkSpaceDTO>> responseEntity = knowledgebaseClient.queryAllSpaceByProject();
        if (responseEntity == null) {
            return;
        }
        List<WorkSpaceDTO> workSpaceDTOList = responseEntity.getBody();
        if (workSpaceDTOList != null && !workSpaceDTOList.isEmpty()) {
            for (WikiRelationDTO wikiRelationDTO : wikiRelationDTOList) {
                for (WorkSpaceDTO workSpaceDTO : workSpaceDTOList) {
                    if (workSpaceDTO.getProjectId() != null && wikiRelationDTO.getProjectId() != null  && wikiRelationDTO.getProjectId().equals(workSpaceDTO.getProjectId()) && wikiRelationDTO.getWikiName() != null && workSpaceDTO.getName() != null && wikiRelationDTO.getWikiName().equals(workSpaceDTO.getName())) {
                        wikiRelationMapper.updateByOptions(wikiRelationDTO.getId(), workSpaceDTO.getId());
                        break;
                    }
                }
            }
        }
        LOGGER.info("==================================== Finished to fix wiki relation by agile! =============================================");
    }
}
