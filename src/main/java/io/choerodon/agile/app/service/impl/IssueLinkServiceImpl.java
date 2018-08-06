package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.IssueLinkCreateDTO;
import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.app.assembler.IssueLinkAssembler;
import io.choerodon.agile.app.service.IssueLinkService;
import io.choerodon.agile.domain.agile.entity.IssueLinkE;
import io.choerodon.agile.domain.agile.repository.IssueLinkRepository;
import io.choerodon.agile.domain.agile.rule.IssueLinkRule;
import io.choerodon.agile.infra.mapper.IssueLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueLinkServiceImpl implements IssueLinkService {

    @Autowired
    private IssueLinkMapper issueLinkMapper;
    @Autowired
    private IssueLinkRepository issueLinkRepository;
    @Autowired
    private IssueLinkRule issueLinkRule;
    @Autowired
    private IssueLinkAssembler issueLinkAssembler;

    @Override
    public List<IssueLinkDTO> createIssueLinkList(List<IssueLinkCreateDTO> issueLinkCreateDTOList, Long issueId, Long projectId) {
        List<IssueLinkE> issueLinkEList = issueLinkAssembler.issueLinkCreateDtoToE(issueLinkCreateDTOList);
        issueLinkEList.forEach(issueLinkE -> {
            issueLinkRule.verifyCreateData(issueLinkE);
            if (issueLinkMapper.selectByPrimaryKey(issueLinkE) == null) {
                issueLinkRepository.create(issueLinkE);
            }
        });
        return listIssueLinkByIssueId(issueId, projectId,false);
    }


    @Override
    public void deleteIssueLink(Long issueLinkId) {
        issueLinkRepository.delete(issueLinkId);
    }

    @Override
    public List<IssueLinkDTO> listIssueLinkByIssueId(Long issueId, Long projectId,Boolean noIssueTest) {
        return issueLinkAssembler.issueLinkDoToDto(issueLinkMapper.queryIssueLinkByIssueId(issueId, projectId,noIssueTest));
    }

    @Override
    public List<IssueLinkDTO> listIssueLinkByBatch(Long projectId, List<Long> issueIds) {
        return issueLinkAssembler.issueLinkDoToDto(issueLinkMapper.listIssueLinkByBatch(projectId, issueIds));
    }

}
