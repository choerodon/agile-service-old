package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.IssueVO;
import io.choerodon.agile.api.vo.MessageDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/9.
 * Email: fuqianghuang01@gmail.com
 */
public interface NoticeService {

    List<MessageDTO> queryByProjectId(Long projectId);

    void updateNotice(Long projectId, List<MessageDTO> messageDTOList);

    List<Long> queryUserIdsByProjectId(Long projectId, String event, IssueVO issueVO);
}
