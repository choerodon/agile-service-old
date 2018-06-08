package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.BoardE;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardRepository {

    BoardE create(BoardE boardE);

    BoardE update(BoardE boardE);

    void delete(Long id);
}
