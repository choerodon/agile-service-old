package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.DataLogCreateVO;
import io.choerodon.agile.api.vo.DataLogVO;
import io.choerodon.agile.infra.dataobject.DataLogDTO;
import io.choerodon.agile.infra.dataobject.DataLogStatusChangeDTO;

import java.util.List;
import java.util.Set;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface DataLogService {

    DataLogVO createDataLog(Long projectId, DataLogCreateVO createVO);

    List<DataLogVO> listByIssueId(Long projectId, Long issueId);

    DataLogDTO create(DataLogDTO dataLogDTO);

    void delete(DataLogDTO dataLogDTO);

    /**
     * 根据id批量删除错误数据
     *
     * @param dataLogIds dataLogIds
     */
    void batchDeleteErrorDataLog(Set<Long> dataLogIds);

    /**
     * 更新脏数据
     *
     * @param dataLogStatusChangeDTOS dataLogStatusChangeDTOS
     */
    void batchUpdateErrorDataLog(Set<DataLogStatusChangeDTO> dataLogStatusChangeDTOS);
}
