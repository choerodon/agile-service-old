package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.IssueLinkE;
import io.choerodon.agile.infra.dataobject.IssueLinkDO;

import java.util.List;


/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
public interface IssueLinkRepository {

    /**
     * 更新敏捷开发Issue链接
     *
     * @param issueLinkE issueLinkE
     * @return IssueLinkE
     */
    IssueLinkE update(IssueLinkE issueLinkE);

    /**
     * 添加一个敏捷开发Issue链接
     *
     * @param issueLinkE issueLinkE
     * @return IssueLinkE
     */
    List<IssueLinkE> create(IssueLinkE issueLinkE);

    /**
     * 根据issueId删除敏捷开发Issue链接
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);

    /**
     * 批量创建issueLink
     *
     * @param issueLinkDOList issueLinkDOList
     * @param issueId         issueId
     */
    void batchCreateIssueLink(List<IssueLinkDO> issueLinkDOList, Long issueId);
}