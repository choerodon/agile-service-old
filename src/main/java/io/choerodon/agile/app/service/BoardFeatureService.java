package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.*;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardFeatureService {

    BoardFeatureInfoVO create(Long projectId, BoardFeatureCreateVO createVO);

    BoardFeatureInfoVO update(Long projectId, Long boardFeatureId, BoardFeatureUpdateVO updateVO);

    BoardFeatureVO queryById(Long projectId, Long boardFeatureId);

    BoardFeatureInfoVO queryInfoById(Long projectId, Long boardFeatureId);

    void deleteById(Long projectId, Long boardFeatureId);

    void deleteByFeatureId(Long projectId, Long featureId);

    ProgramBoardInfoDTO queryBoardInfo(Long programId, ProgramBoardFilterDTO boardFilter);

    void checkId(Long projectId, Long boardFeatureId);
}
