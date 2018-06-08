package io.choerodon.agile.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.domain.agile.repository.VersionIssueRelRepository;
import io.choerodon.agile.infra.dataobject.VersionIssueRelDO;
import io.choerodon.agile.infra.mapper.VersionIssueRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
@Component
@Transactional(rollbackFor = CommonException.class)
public class VersionIssueRelRepositoryImpl implements VersionIssueRelRepository {

    private static final String UPDATE_ERROR = "error.VersionIssueRel.update";
    private static final String INSERT_ERROR = "error.VersionIssueRel.create";

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;

    @Override
    public List<VersionIssueRelE> update(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelDO versionIssueRelDO = ConvertHelper.convert(versionIssueRelE, VersionIssueRelDO.class);
        if (versionIssueRelMapper.updateByPrimaryKeySelective(versionIssueRelDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        VersionIssueRelDO versionIssueRelDO1 = new VersionIssueRelDO();
        versionIssueRelDO.setVersionId(versionIssueRelDO.getVersionId());
        return ConvertHelper.convertList(versionIssueRelMapper.select(versionIssueRelDO1), VersionIssueRelE.class);
    }

    @Override
    public List<VersionIssueRelE> create(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelDO versionIssueRelDO = ConvertHelper.convert(versionIssueRelE, VersionIssueRelDO.class);
        if (versionIssueRelMapper.insert(versionIssueRelDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        VersionIssueRelDO versionIssueRelDO1 = new VersionIssueRelDO();
        versionIssueRelDO.setVersionId(versionIssueRelDO.getVersionId());
        return ConvertHelper.convertList(versionIssueRelMapper.select(versionIssueRelDO1), VersionIssueRelE.class);
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
        versionIssueRelDO.setIssueId(issueId);
        return versionIssueRelMapper.delete(versionIssueRelDO);
    }

    @Override
    public int deleteByVersionId(Long projectId,Long versionId) {
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
        versionIssueRelDO.setProjectId(projectId);
        versionIssueRelDO.setVersionId(versionId);
        return versionIssueRelMapper.delete(versionIssueRelDO);
    }

    @Override
    public Boolean deleteIncompleteIssueByVersionId(Long projectId, Long versionId) {
        versionIssueRelMapper.deleteIncompleteIssueByVersionId(projectId, versionId);
        return true;
    }
}