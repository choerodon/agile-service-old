package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.PiE;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiRepository {

    PiE create(PiE piE);

    PiE updateBySelective(PiE piE);

    void delete(Long piId);
}
