package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.LabelIssueRelDTO;



/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
public interface LabelIssueRelService {

    /**
     * 添加一个敏捷开发Issue标签关联
     *
     * @param labelIssueRelDTO labelIssueRelDTO
     * @return LabelIssueRelE
     */
    LabelIssueRelDTO create(LabelIssueRelDTO labelIssueRelDTO);

    /**
     * 根据issueId删除labelIssue
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);

    /**
     * 根据issueId批量删除labelIssue
     *
     * @param issueId issueId
     * @return int
     */
    int batchDeleteByIssueId(Long issueId);

    /**
     * 根据查询条件删除
     * @param labelIssueRelDTO labelIssueRelDTO
     * @return int
     */
    int delete(LabelIssueRelDTO labelIssueRelDTO);
}