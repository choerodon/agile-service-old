package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.RankVO;
import io.choerodon.agile.infra.dataobject.RankDTO;

public interface RankService {

    RankDTO getReferenceRank(Long projectId, String type, Long referenceIssueId);

    void epicAndFeatureRank(Long projectId, RankVO rankVO);
}
