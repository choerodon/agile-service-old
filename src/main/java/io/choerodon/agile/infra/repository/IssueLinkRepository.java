package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.IssueLinkE;

import java.util.List;


/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
public interface IssueLinkRepository {

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
     * 删除issue链接
     *
     * @param issueLinkId issueLinkId
     * @return int
     */
    int delete(Long issueLinkId);

}