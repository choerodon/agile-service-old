package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.OrganizationDTO;
import io.choerodon.agile.api.dto.WikiMenuDTO;
import io.choerodon.agile.api.dto.WikiRelationDTO;
import io.choerodon.agile.app.service.WikiRelationService;
import io.choerodon.agile.domain.agile.entity.WikiRelationE;
import io.choerodon.agile.domain.agile.repository.WikiRelationRepository;
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
public class WikiRelationServiceImpl implements WikiRelationService {

    @Autowired
    private WikiRelationRepository wikiRelationRepository;

    @Autowired
    private WikiRelationMapper wikiRelationMapper;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Autowired
    private UserFeignClient userFeignClient;

    @Value("${services.wiki.host}")
    private String wikiHost;

    @Value("${services.wiki.token}")
    private String wikiToken;

    private Boolean checkRepeat(WikiRelationE wikiRelationE) {
        WikiRelationDO wikiRelationDO = new WikiRelationDO();
        wikiRelationDO.setProjectId(wikiRelationE.getProjectId());
        wikiRelationDO.setIssueId(wikiRelationE.getIssueId());
        wikiRelationDO.setWikiUrl(wikiRelationE.getWikiUrl());
        WikiRelationDO res = wikiRelationMapper.selectOne(wikiRelationDO);
        if (res == null) {
            return false;
        } else {
            return true;
        }
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

    @Override
    public String queryWikiMenus(Long projectId, WikiMenuDTO wikiMenuDTO) {
        String url = wikiHost + "/bin/get";
        String param = "outputSyntax=plain&sheet=XWiki.DocumentTree&showAttachments=false&showTranslations=false&data=children&id=";
        if (wikiMenuDTO.getMenuId() == null) {
            ResponseEntity<OrganizationDTO> organizationDTOResponseEntity = userFeignClient.query(wikiMenuDTO.getOrganizationId());
            if (organizationDTOResponseEntity == null) {
                throw new CommonException("error.organization.get");
            }
            OrganizationDTO organizationDTO = organizationDTOResponseEntity.getBody();
            String organizationName = null;
            String projectName = null;
            try {
                organizationName = URLEncoder.encode(organizationDTO.getName(), "utf-8");
                projectName = URLEncoder.encode(wikiMenuDTO.getProjectName(), "utf-8");
            } catch (UnsupportedEncodingException u) {
                throw new CommonException(u);
            }
            param = param + "document:xwiki:O-" + organizationName + ".P-" + projectName + ".WebHome";
        } else {
            param = param + wikiMenuDTO.getMenuId();
        }
        Map<String, String> otherHeaders = new HashMap<>();
        otherHeaders.put("username", wikiMenuDTO.getUsername());
        otherHeaders.put("wikitoken", wikiToken);
        return httpRequestUtil.sendGet(url, param, otherHeaders);
    }
}
