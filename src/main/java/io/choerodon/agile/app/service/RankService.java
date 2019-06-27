package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.RankDTO;
import io.choerodon.agile.infra.dataobject.RankDO;

public interface RankService {

    RankDO getReferenceRank(Long projectId, String type, Long referenceIssueId);

    void epicAndFeatureRank(Long projectId, RankDTO rankDTO);
}
