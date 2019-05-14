package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.BoardFeatureCreateDTO;
import io.choerodon.agile.api.dto.BoardFeatureDTO;
import io.choerodon.agile.api.dto.BoardFeatureUpdateDTO;
import io.choerodon.agile.api.dto.ProgramBoardInfoDTO;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardFeatureService {

    BoardFeatureDTO create(Long projectId, BoardFeatureCreateDTO createDTO);

    BoardFeatureDTO update(Long projectId, Long boardFeatureId, BoardFeatureUpdateDTO updateDTO);

    BoardFeatureDTO queryById(Long projectId, Long boardFeatureId);

    void deleteById(Long projectId, Long boardFeatureId);

    ProgramBoardInfoDTO queryBoardInfo(Long projectId);
}
