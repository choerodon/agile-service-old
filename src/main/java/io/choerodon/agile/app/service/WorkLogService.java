package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.WorkLogDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
public interface WorkLogService {

    WorkLogDTO create(Long projectId, WorkLogDTO workLogDTO);

    WorkLogDTO update(Long projectId, Long logId, WorkLogDTO workLogDTO);

    void delete(Long projectId, Long logId);

    WorkLogDTO queryWorkLogById(Long projectId, Long logId);

    List<WorkLogDTO> queryWorkLogListByIssueId(Long projectId, Long issueId);
}
