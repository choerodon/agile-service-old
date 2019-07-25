package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.infra.annotation.DataLog;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.VersionIssueRelService;
import io.choerodon.agile.infra.dataobject.VersionIssueRelDTO;
import io.choerodon.agile.infra.mapper.VersionIssueRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
@Service
public class VersionIssueRelServiceImpl implements VersionIssueRelService {

    private static final String INSERT_ERROR = "error.VersionIssueRel.create";

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;

    @Override
    @DataLog(type = "versionCreate")
    public VersionIssueRelDTO create(VersionIssueRelDTO versionIssueRelDTO) {
        if (versionIssueRelMapper.insert(versionIssueRelDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        VersionIssueRelDTO query = new VersionIssueRelDTO();
        query.setVersionId(versionIssueRelDTO.getVersionId());
        query.setIssueId(versionIssueRelDTO.getIssueId());
        query.setProjectId(versionIssueRelDTO.getProjectId());
        query.setRelationType(versionIssueRelDTO.getRelationType());
        return versionIssueRelMapper.selectOne(query);
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        versionIssueRelDTO.setIssueId(issueId);
        return versionIssueRelMapper.delete(versionIssueRelDTO);
    }

    @Override
    @DataLog(type = "batchDeleteVersion", single = false)
    public int batchDeleteByIssueIdAndTypeArchivedExceptInfluence(VersionIssueRelDTO versionIssueRelDTO) {
        return versionIssueRelMapper.batchDeleteByIssueIdAndTypeArchivedExceptInfluence(versionIssueRelDTO.getProjectId(),
                versionIssueRelDTO.getIssueId(), versionIssueRelDTO.getRelationType());
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