package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.WorkLogDTO;

public interface IWorkLogService {

    WorkLogDTO createBase(WorkLogDTO workLogDTO);

    void deleteBase(Long projectId,Long logId);
}
