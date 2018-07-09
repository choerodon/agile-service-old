package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;

import java.util.List;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
public interface VersionIssueRelRepository {

    /**
     * 更新
     *
     * @param versionIssueRelE versionIssueRelE
     * @return VersionIssueRelE
     */
    List<VersionIssueRelE> update(VersionIssueRelE versionIssueRelE);

    /**
     * 添加一个
     *
     * @param versionIssueRelE versionIssueRelE
     * @return VersionIssueRelE
     */
    List<VersionIssueRelE> create(VersionIssueRelE versionIssueRelE);

    /**
     * 根据issueId删除版本
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);

    /**
     * 通过查询条件批量删除issue版本关联（不包含已归档的版本）
     *
     * @param projectId   projectId
     * @param issueId     issueId
     * @param versionType versionType
     * @return int
     */
    int batchDeleteByIssueIdAndType(Long projectId, Long issueId, String versionType);

    /**
     * 根据id删除
     *
     * @param projectId projectId
     * @param versionId versionId
     * @return int
     */
    int deleteByVersionId(Long projectId, Long versionId);

    Boolean deleteIncompleteIssueByVersionId(Long projectId, Long versionId);

    int deleteByVersionIds(Long projectId, List<Long> versionIds);
}