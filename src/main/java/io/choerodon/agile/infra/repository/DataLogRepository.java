package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.infra.dataobject.DataLogStatusChangeDTO;

import java.util.Set;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface DataLogRepository {

    DataLogE create(DataLogE dataLogE);

    void delete(DataLogE dataLogE);

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
