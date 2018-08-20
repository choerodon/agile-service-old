package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.BatchRemoveSprintE;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.domain.service.IIssueService;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.MoveIssueDO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@Component
public class IssueRepositoryImpl implements IssueRepository {

    private static final String UPDATE_ERROR = "error.Issue.update";
    private static final String INSERT_ERROR = "error.Issue.create";
    private static final String DELETE_ERROR = "error.Issue.delete";

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private IIssueService iIssueService;

    @Override
    @DataLog(type = "issue")
    public IssueE update(IssueE issueE, String[] fieldList) {
        IssueDO issueDO = ConvertHelper.convert(issueE, IssueDO.class);
        if (iIssueService.updateOptional(issueDO, fieldList) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueDO.getIssueId()), IssueE.class);
    }

    @Override
    @DataLog(type = "issueCreate")
    public IssueE create(IssueE issueE) {
        IssueDO issueDO = ConvertHelper.convert(issueE, IssueDO.class);
        if (issueMapper.insert(issueDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueDO.getIssueId()), IssueE.class);
    }

    @Override
    public int delete(Long projectId, Long issueId) {
        IssueDO issueDO = new IssueDO();
        issueDO.setProjectId(projectId);
        issueDO.setIssueId(issueId);
        IssueDO issueDO1 = issueMapper.selectOne(issueDO);
        int isDelete = issueMapper.delete(issueDO1);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return isDelete;
    }

    @Override
    @DataLog(type = "batchRemoveSprintBySprintId", single = false)
    public Boolean batchRemoveFromSprint(Long projectId, Long sprintId) {
        issueMapper.removeFromSprint(projectId, sprintId);
        return true;
    }

    @Override
    @DataLog(type = "batchToVersion", single = false)
    public Boolean batchIssueToVersion(VersionIssueRelE versionIssueRelE) {
        issueMapper.batchIssueToVersion(versionIssueRelE.getProjectId(), versionIssueRelE.getVersionId(), versionIssueRelE.getIssueIds(), versionIssueRelE.getCreationDate(), versionIssueRelE.getCreatedBy());
        return true;
    }

    @Override
    @DataLog(type = "batchToEpic", single = false)
    public Boolean batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds) {
        issueMapper.batchIssueToEpic(projectId, epicId, issueIds);
        return true;
    }

    @Override
    @DataLog(type = "batchRemoveVersion", single = false)
    public int batchRemoveVersion(Long projectId, List<Long> issueIds) {
        return issueMapper.batchRemoveFromVersion(projectId, issueIds);
    }

    @Override
    public int batchUpdateIssueEpicId(Long projectId, Long issueId) {
        return issueMapper.batchUpdateIssueEpicId(projectId, issueId);
    }

    @Override
    public int issueToDestinationByIds(Long projectId, Long sprintId, List<Long> issueIds, Date date, Long userId) {
        return issueMapper.issueToDestinationByIds(projectId, sprintId, issueIds, date, userId);
    }

    @Override
    public int batchUpdateIssueRank(Long projectId, List<MoveIssueDO> moveIssues) {
        return issueMapper.batchUpdateIssueRank(projectId, moveIssues);
    }

    @Override
    @DataLog(type = "batchRemoveSprint", single = false)
    public int removeIssueFromSprintByIssueIds(BatchRemoveSprintE batchRemoveSprintE) {
        return issueMapper.removeIssueFromSprintByIssueIds(batchRemoveSprintE.getProjectId(), batchRemoveSprintE.getIssueIds());
    }

    @Override
    public int deleteIssueFromSprintByIssueId(Long projectId, Long issueId) {
        return issueMapper.deleteIssueFromSprintByIssueId(projectId, issueId);
    }

    @Override
    public int batchUpdateSequence(Integer sequence, Long projectId, Integer add, Long issueId) {
        return issueMapper.batchUpdateSequence(sequence, projectId, add, issueId);
    }

    @Override
    @DataLog(type = "batchRemoveSprintToTarget", single = false)
    public int issueToDestinationByIdsCloseSprint(Long projectId, Long targetSprintId, List<Long> issueIds, Date date, Long userId) {
        return issueMapper.issueToDestinationByIds(projectId, targetSprintId, issueIds, date, userId);
    }
}