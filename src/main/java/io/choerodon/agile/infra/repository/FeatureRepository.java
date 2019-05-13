package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.FeatureE;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/13.
 * Email: fuqianghuang01@gmail.com
 */
public interface FeatureRepository {

    FeatureE create(FeatureE featureE);

    FeatureE updateSelective(FeatureE featureE);

    void delete(Long issueId);
}
