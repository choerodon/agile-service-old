package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.FeatureDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/13.
 * Email: fuqianghuang01@gmail.com
 */
public interface FeatureService {

    FeatureDTO create(FeatureDTO featureDTO);

    FeatureDTO updateSelective(FeatureDTO featureDTO);

    void delete(Long issueId);
}
