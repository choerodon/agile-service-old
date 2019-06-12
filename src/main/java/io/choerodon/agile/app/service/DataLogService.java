package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.DataLogCreateDTO;
import io.choerodon.agile.api.dto.DataLogDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface DataLogService {

    DataLogDTO create(Long projectId, DataLogCreateDTO createDTO);

    List<DataLogDTO> listByIssueId(Long projectId, Long issueId);

}
