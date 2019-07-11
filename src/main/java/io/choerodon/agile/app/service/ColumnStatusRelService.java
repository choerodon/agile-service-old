package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.ColumnStatusRelDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/9.
 * Email: fuqianghuang01@gmail.com
 */
public interface ColumnStatusRelService {

    void create(ColumnStatusRelDTO columnStatusRelDTO);

    void delete(ColumnStatusRelDTO columnStatusRelDTO);
}
