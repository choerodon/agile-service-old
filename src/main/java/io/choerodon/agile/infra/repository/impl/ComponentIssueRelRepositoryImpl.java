package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
import io.choerodon.agile.infra.repository.ComponentIssueRelRepository;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDO;
import io.choerodon.agile.infra.mapper.ComponentIssueRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:47:27
 */
@Component
public class ComponentIssueRelRepositoryImpl implements ComponentIssueRelRepository {

    private static final String INSERT_ERROR = "error.ComponentIssueRel.create";

    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;

    @Override
    @DataLog(type = "componentCreate")
    public ComponentIssueRelE create(ComponentIssueRelE componentIssueRelE) {
        ComponentIssueRelDO componentIssueRelDO = ConvertHelper.convert(componentIssueRelE, ComponentIssueRelDO.class);
        if (componentIssueRelMapper.insert(componentIssueRelDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        ComponentIssueRelDO query = new ComponentIssueRelDO();
        query.setComponentId(componentIssueRelDO.getComponentId());
        query.setIssueId(componentIssueRelDO.getIssueId());
        query.setProjectId(componentIssueRelDO.getProjectId());
        return ConvertHelper.convert(componentIssueRelMapper.selectOne(query), ComponentIssueRelE.class);
    }

    @Override
    @DataLog(type = "batchComponentDelete", single = false)
    public int batchComponentDelete(Long issueId) {
        return deleteComponentIssueRel(issueId);
    }

    @Override
    @DataLog(type = "componentDelete")
    public int delete(ComponentIssueRelDO componentIssueRelDO) {
        return componentIssueRelMapper.delete(componentIssueRelDO);
    }

    @Override
    public void deleteByComponentId(Long projectId, Long componentId) {
        ComponentIssueRelDO componentIssueRelDO = new ComponentIssueRelDO();
        componentIssueRelDO.setProjectId(projectId);
        componentIssueRelDO.setComponentId(componentId);
        if (!componentIssueRelMapper.select(componentIssueRelDO).isEmpty()
                && componentIssueRelMapper.delete(componentIssueRelDO) == 0) {
            throw new CommonException("error.componentIssueRel.delete");
        }

    }

    @Override
    public int deleteByIssueId(Long issueId) {
        return deleteComponentIssueRel(issueId);
    }

    private int deleteComponentIssueRel(Long issueId) {
        ComponentIssueRelDO componentIssueRelDO = new ComponentIssueRelDO();
        componentIssueRelDO.setIssueId(issueId);
        return componentIssueRelMapper.delete(componentIssueRelDO);
    }
}