package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.IssueAttachmentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueAttachmentService {

    List<IssueAttachmentVO> create(Long projectId, Long issueId, HttpServletRequest request);

    Boolean delete(Long projectId, Long issueAttachmentId);

    List<String> uploadForAddress(Long projectId, HttpServletRequest request);

    /**
     * 根据issueId删除附件
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);

    /**
     * 生成issueAttachment记录并生成日志（用于复制issue）
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @param fileName  fileName
     * @param url       url
     */
    void dealIssue(Long projectId, Long issueId, String fileName, String url);
}
