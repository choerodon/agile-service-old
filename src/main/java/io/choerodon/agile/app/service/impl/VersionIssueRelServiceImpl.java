package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.app.service.VersionIssueRelService;
import io.choerodon.agile.infra.dataobject.VersionIssueRelDTO;
import io.choerodon.agile.infra.mapper.VersionIssueRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
@Component
public class VersionIssueRelServiceImpl implements VersionIssueRelService {

    private static final String INSERT_ERROR = "error.VersionIssueRel.create";

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;

    @Override
    @DataLog(type = "versionCreate")
    public VersionIssueRelE create(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelDTO versionIssueRelDTO = ConvertHelper.convert(versionIssueRelE, VersionIssueRelDTO.class);
        if (versionIssueRelMapper.insert(versionIssueRelDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        VersionIssueRelDTO query = new VersionIssueRelDTO();
        query.setVersionId(versionIssueRelDTO.getVersionId());
        query.setIssueId(versionIssueRelDTO.getIssueId());
        query.setProjectId(versionIssueRelDTO.getProjectId());
        query.setRelationType(versionIssueRelDTO.getRelationType());
        return ConvertHelper.convert(versionIssueRelMapper.selectOne(query), VersionIssueRelE.class);
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        versionIssueRelDTO.setIssueId(issueId);
        return versionIssueRelMapper.delete(versionIssueRelDTO);
    }

    @Override
    @DataLog(type = "batchDeleteVersion", single = false)
    public int batchDeleteByIssueIdAndTypeArchivedExceptInfluence(VersionIssueRelE versionIssueRelE) {
        return versionIssueRelMapper.batchDeleteByIssueIdAndTypeArchivedExceptInfluence(versionIssueRelE.getProjectId(),
                versionIssueRelE.getIssueId(), versionIssueRelE.getRelationType());
    }

    @Override
    @DataLog(type = "batchDeleteByVersionId", single = false)
    public int deleteByVersionId(Long projectId, Long versionId) {
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        versionIssueRelDTO.setProjectId(projectId);
        versionIssueRelDTO.setVersionId(versionId);
        return versionIssueRelMapper.delete(versionIssueRelDTO);
    }

    @Override
    @DataLog(type = "batchVersionDeleteByIncompleteIssue", single = false)
    public Boolean deleteIncompleteIssueByVersionId(Long projectId, Long versionId) {
        versionIssueRelMapper.deleteIncompleteIssueByVersionId(projectId, versionId);
        return true;
    }

    @Override
    @DataLog(type = "batchVersionDeleteByVersionIds", single = false)
    public int deleteByVersionIds(Long projectId, List<Long> versionIds) {
        return versionIssueRelMapper.deleteByVersionIds(projectId, versionIds);
    }

    @Override
    @DataLog(type = "versionDelete")
    public int delete(VersionIssueRelDTO versionIssueRelDTO) {
        return versionIssueRelMapper.delete(versionIssueRelDTO);
    }
}