package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.DataLogCreateVO;
import io.choerodon.agile.api.vo.DataLogVO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface DataLogService {

    DataLogVO create(Long projectId, DataLogCreateVO createDTO);

    List<DataLogVO> listByIssueId(Long projectId, Long issueId);

}
