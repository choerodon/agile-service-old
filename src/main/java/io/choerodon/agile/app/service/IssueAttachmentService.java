package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.IssueAttachmentDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueAttachmentService {

    List<IssueAttachmentDTO> create(Long projectId, Long issueId, HttpServletRequest request);

    Boolean delete(Long projectId, Long issueAttachmentId);

    List<String> uploadForAddress(Long projectId, HttpServletRequest request);

    /**
     * 根据issueId删除附件
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);
}
