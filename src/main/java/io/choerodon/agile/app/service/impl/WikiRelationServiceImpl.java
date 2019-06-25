package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.OrganizationDTO;
import io.choerodon.agile.api.dto.WikiMenuDTO;
import io.choerodon.agile.api.dto.WikiRelationDTO;
import io.choerodon.agile.app.service.WikiRelationService;
import io.choerodon.agile.domain.agile.entity.WikiRelationE;
import io.choerodon.agile.infra.dataobject.WorkSpaceDO;
import io.choerodon.agile.infra.feign.KnowledgebaseClient;
import io.choerodon.agile.infra.repository.WikiRelationRepository;
import io.choerodon.agile.infra.common.utils.HttpRequestUtil;
import io.choerodon.agile.infra.dataobject.WikiRelationDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.WikiRelationMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WikiRelationServiceImpl implements WikiRelationService {

    private static final String ENC = "utf-8";

    @Autowired
    private WikiRelationRepository wikiRelationRepository;

    @Autowired
    private WikiRelationMapper wikiRelationMapper;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private KnowledgebaseClient knowledgebaseClient;

    @Value("${services.wiki.host}")
    private String wikiHost;

    @Value("${services.wiki.token}")
    private String wikiToken;

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
        jsonObject.put("wikiHost", wikiHost);
        jsonObject.put("wikiRelationList", ConvertHelper.convertList(wikiRelationDOList, WikiRelationDTO.class));
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
    public String queryWikiMenus(Long projectId, WikiMenuDTO wikiMenuDTO) {
        String url = wikiHost + "/bin/get";
        String param = "outputSyntax=plain&sheet=XWiki.DocumentTree&showAttachments=false&showTranslations=false&data=children&limit=999&id=";
        if (wikiMenuDTO.getMenuId() == null) {
            ResponseEntity<OrganizationDTO> organizationDTOResponseEntity = userFeignClient.query(wikiMenuDTO.getOrganizationId());
            if (organizationDTOResponseEntity == null) {
                throw new CommonException("error.organization.get");
            }
            OrganizationDTO organizationDTO = organizationDTOResponseEntity.getBody();
            String organizationName = null;
            String projectName = null;
            try {
                organizationName = URLEncoder.encode(organizationDTO.getName(), ENC);
                projectName = URLEncoder.encode(wikiMenuDTO.getProjectName(), ENC);
            } catch (UnsupportedEncodingException u) {
                throw new CommonException(u.getMessage());
            }
            param = param + "document:xwiki:O-" + organizationName + ".P-" + projectName + ".WebHome";
        } else {
            String menuIdStr = null;
            try {
                menuIdStr = URLEncoder.encode(wikiMenuDTO.getMenuId(), ENC);
            } catch (UnsupportedEncodingException n) {
                throw new CommonException(n.getMessage());
            }
            param = param + menuIdStr;
        }
        Map<String, String> otherHeaders = new HashMap<>();
        otherHeaders.put("username", wikiMenuDTO.getUsername());
        otherHeaders.put("wikitoken", wikiToken);
        return httpRequestUtil.sendGet(url, param, otherHeaders);
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
                    if (workSpaceDO.getProjectId() != null && wikiRelationDO.getProjectId().equals(workSpaceDO.getProjectId()) && wikiRelationDO.getWikiName().equals(workSpaceDO.getName())) {
                        wikiRelationMapper.updateByOptions(wikiRelationDO.getId(), workSpaceDO.getId());
                        break;
                    }
                }
            }
        }
    }
}
