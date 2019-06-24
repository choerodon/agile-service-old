package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.RankDTO;

public interface RankService {

    void epicAndFeatureRank(Long projectId, RankDTO rankDTO);
}
