package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.IssueLinkCreateVO;
import io.choerodon.agile.api.vo.IssueLinkVO;
import io.choerodon.agile.api.validator.IssueLinkValidator;
import io.choerodon.agile.app.assembler.IssueLinkAssembler;
import io.choerodon.agile.app.service.IssueLinkService;
import io.choerodon.agile.infra.dataobject.IssueLinkDTO;
import io.choerodon.agile.infra.mapper.IssueLinkMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueLinkServiceImpl implements IssueLinkService {

    private static final String INSERT_ERROR = "error.IssueLink.create";

    @Autowired
    private IssueLinkMapper issueLinkMapper;
    @Autowired
    private IssueLinkValidator issueLinkValidator;
    @Autowired
    private IssueLinkAssembler issueLinkAssembler;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<IssueLinkVO> createIssueLinkList(List<IssueLinkCreateVO> issueLinkCreateVOList, Long issueId, Long projectId) {
        List<IssueLinkDTO> issueLinkDTOList = issueLinkAssembler.toTargetList(issueLinkCreateVOList, IssueLinkDTO.class);
        issueLinkDTOList.forEach(issueLinkDTO -> {
            issueLinkDTO.setProjectId(projectId);
            issueLinkValidator.verifyCreateData(issueLinkDTO);
            if (issueLinkValidator.checkUniqueLink(issueLinkDTO)) {
                create(issueLinkDTO);
            }
        });
        return listIssueLinkByIssueId(issueId, projectId, false);
    }


    @Override
    public void deleteIssueLink(Long issueLinkId) {
        delete(issueLinkId);
    }

    @Override
    public List<IssueLinkVO> listIssueLinkByIssueId(Long issueId, Long projectId, Boolean noIssueTest) {
        return issueLinkAssembler.issueLinkDoToDto(projectId, issueLinkMapper.queryIssueLinkByIssueId(issueId, projectId, noIssueTest));
    }

    @Override
    public List<IssueLinkVO> listIssueLinkByBatch(Long projectId, List<Long> issueIds) {
        return issueLinkAssembler.issueLinkDoToDto(projectId, issueLinkMapper.listIssueLinkByBatch(projectId, issueIds));
    }

    @Override
    public List<IssueLinkDTO> create(IssueLinkDTO issueLinkDTO) {
        if (issueLinkMapper.insert(issueLinkDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        IssueLinkDTO query = new IssueLinkDTO();
        query.setIssueId(issueLinkDTO.getIssueId());
        return modelMapper.map(issueLinkMapper.select(query), new TypeToken<List<IssueLinkDTO>>(){}.getType());
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        return issueLinkMapper.deleteByIssueId(issueId);
    }

    @Override
    public int delete(Long issueLinkId) {
        return issueLinkMapper.deleteByPrimaryKey(issueLinkId);
    }

}
