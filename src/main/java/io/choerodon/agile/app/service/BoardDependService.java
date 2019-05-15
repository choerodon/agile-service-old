package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.BoardDependCreateDTO;
import io.choerodon.agile.api.dto.BoardDependDTO;
import io.choerodon.agile.api.dto.BoardDependUpdateDTO;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardDependService {

    BoardDependDTO create(Long projectId, BoardDependCreateDTO createDTO);

    BoardDependDTO update(Long projectId, Long boardDependId, BoardDependUpdateDTO updateDTO);

    BoardDependDTO queryById(Long projectId, Long boardDependId);

    void deleteById(Long projectId, Long boardDependId);
}
