package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.LabelIssueRelE;
import io.choerodon.agile.infra.dataobject.LabelIssueRelDO;

import java.util.List;


/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
public interface LabelIssueRelRepository {

    /**
     * 添加一个敏捷开发Issue标签关联
     *
     * @param labelIssueRelE labelIssueRelE
     * @return LabelIssueRelE
     */
    LabelIssueRelE create(LabelIssueRelE labelIssueRelE);

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
     * @param labelIssueRelDO labelIssueRelDO
     * @return int
     */
    int delete(LabelIssueRelDO labelIssueRelDO);
}