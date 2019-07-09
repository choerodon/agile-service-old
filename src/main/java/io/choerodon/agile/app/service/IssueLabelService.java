package io.choerodon.agile.app.service;


import io.choerodon.agile.api.vo.IssueLabelVO;

import java.util.List;

/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
public interface IssueLabelService {

    /**
     * 根据项目id查询issueLabel
     *
     * @param projectId projectId
     * @return IssueLabelVO
     */
    List<IssueLabelVO> listIssueLabel(Long projectId);
}