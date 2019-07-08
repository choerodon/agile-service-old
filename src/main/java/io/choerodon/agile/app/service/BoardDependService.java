package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.BoardDependCreateDTO;
import io.choerodon.agile.api.vo.BoardDependDTO;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardDependService {

    BoardDependDTO create(Long projectId, BoardDependCreateDTO createDTO);

    BoardDependDTO queryById(Long projectId, Long boardDependId);

    void deleteById(Long projectId, Long boardDependId);
}
