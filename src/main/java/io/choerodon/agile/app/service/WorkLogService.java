package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.WorkLogVO;
import io.choerodon.agile.infra.dataobject.WorkLogDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
public interface WorkLogService {

    WorkLogVO createWorkLog(Long projectId, WorkLogVO workLogVO);

    WorkLogVO updateWorkLog(Long projectId, Long logId, WorkLogVO workLogVO);

    void deleteWorkLog(Long projectId, Long logId);

    WorkLogVO queryWorkLogById(Long projectId, Long logId);

    List<WorkLogVO> queryWorkLogListByIssueId(Long projectId, Long issueId);

//    WorkLogDTO createBase(WorkLogDTO workLogDTO);

    WorkLogDTO updateBase(WorkLogDTO workLogDTO);

//    void deleteBase(Long projectId,Long logId);
}
