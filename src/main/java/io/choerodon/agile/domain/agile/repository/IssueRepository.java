package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.infra.dataobject.MoveIssueDO;

import java.util.Date;
import java.util.List;


/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public interface IssueRepository {

    /**
     * 按照字段更新敏捷开发Issue部分字段
     *
     * @param issueE    issueE
     * @param fieldList fieldList
     * @return IssueE
     */
    IssueE update(IssueE issueE, String[] fieldList);

    /**
     * 添加一个敏捷开发Issue
     *
     * @param issueE issueE
     * @return IssueE
     */
    IssueE create(IssueE issueE);

    /**
     * 根据id删除敏捷开发Issue
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return int
     */
    int delete(Long projectId, Long issueId);

    Boolean removeFromSprint(Long projectId, Long sprintId);

    IssueE updateSelective(IssueE issueE);

    Boolean batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds);

    Boolean batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds);

    int batchRemoveVersion(Long projectId, List<Long> issueIds);

    /**
     * 将该epic下的issue的epicId设为0
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return int
     */
    int batchUpdateIssueEpicId(Long projectId, Long issueId);

    int issueToDestinationByIds(Long projectId, Long sprintId, List<Long> issueIds, Date date);

    int batchUpdateIssueRank(Long projectId, List<MoveIssueDO> moveIssueDOS);

    int removeIssueFromSprintByIssueIds(Long projectId, List<Long> issueIds);

    int issueToSprint(Long projectId, Long sprintId, Long issueId, Date date);

    int deleteIssueFromSprintByIssueId(Long projectId, Long issueId);
}