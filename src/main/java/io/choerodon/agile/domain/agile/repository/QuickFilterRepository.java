package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.QuickFilterE;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public interface QuickFilterRepository {

    QuickFilterE create(QuickFilterE quickFilterE);

    QuickFilterE update(QuickFilterE quickFilterE);

    void deleteById(Long filterId);

}
