package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.*;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardFeatureService {

    BoardFeatureInfoDTO create(Long projectId, BoardFeatureCreateDTO createDTO);

    BoardFeatureInfoDTO update(Long projectId, Long boardFeatureId, BoardFeatureUpdateDTO updateDTO);

    BoardFeatureDTO queryById(Long projectId, Long boardFeatureId);

    BoardFeatureInfoDTO queryInfoById(Long projectId, Long boardFeatureId);

    void deleteById(Long projectId, Long boardFeatureId);

    void deleteByFeatureId(Long projectId, Long featureId);

    ProgramBoardInfoDTO queryBoardInfo(Long projectId);
}
