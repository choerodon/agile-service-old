package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.ComponentIssueRelService;
import io.choerodon.agile.infra.mapper.ComponentIssueRelMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:47:27
 */
@Component
public class ComponentIssueRelServiceImpl implements ComponentIssueRelService {

    private static final String INSERT_ERROR = "error.ComponentIssueRel.create";

    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    @DataLog(type = "componentCreate")
    public ComponentIssueRelDTO create(ComponentIssueRelDTO componentIssueRelDTO) {
        if (componentIssueRelMapper.insert(componentIssueRelDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        ComponentIssueRelDTO query = new ComponentIssueRelDTO();
        query.setComponentId(componentIssueRelDTO.getComponentId());
        query.setIssueId(componentIssueRelDTO.getIssueId());
        query.setProjectId(componentIssueRelDTO.getProjectId());
        return modelMapper.map(componentIssueRelMapper.selectOne(query), ComponentIssueRelDTO.class);
    }

    @Override
    @DataLog(type = "batchComponentDelete", single = false)
    public int batchComponentDelete(Long issueId) {
        return deleteComponentIssueRel(issueId);
    }

    @Override
    @DataLog(type = "componentDelete")
    public int delete(ComponentIssueRelDTO componentIssueRelDTO) {
        return componentIssueRelMapper.delete(componentIssueRelDTO);
    }

    @Override
    public void deleteByComponentId(Long projectId, Long componentId) {
        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
        componentIssueRelDTO.setProjectId(projectId);
        componentIssueRelDTO.setComponentId(componentId);
        if (!componentIssueRelMapper.select(componentIssueRelDTO).isEmpty()
                && componentIssueRelMapper.delete(componentIssueRelDTO) == 0) {
            throw new CommonException("error.componentIssueRel.delete");
        }

    }

    @Override
    public int deleteByIssueId(Long issueId) {
        return deleteComponentIssueRel(issueId);
    }

    private int deleteComponentIssueRel(Long issueId) {
        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
        componentIssueRelDTO.setIssueId(issueId);
        return componentIssueRelMapper.delete(componentIssueRelDTO);
    }
}