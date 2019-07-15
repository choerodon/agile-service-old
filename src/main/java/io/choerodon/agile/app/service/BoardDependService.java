package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.BoardDependCreateVO;
import io.choerodon.agile.api.vo.BoardDependVO;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardDependService {

    BoardDependVO create(Long projectId, BoardDependCreateVO createVO);

    BoardDependVO queryById(Long projectId, Long boardDependId);

    void deleteById(Long projectId, Long boardDependId);
}
