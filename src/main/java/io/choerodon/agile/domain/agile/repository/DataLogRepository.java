package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.DataLogE;

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
}
