package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
public interface ColumnStatusRelRepository {

    void create(ColumnStatusRelE columnStatusRelE);

    void delete(ColumnStatusRelE columnStatusRelE);
}
