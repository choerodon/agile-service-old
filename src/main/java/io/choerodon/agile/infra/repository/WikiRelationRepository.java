package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.WikiRelationE;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
public interface WikiRelationRepository {

    void create(WikiRelationE wikiRelationE);

    void delete(WikiRelationE wikiRelationE);
}
