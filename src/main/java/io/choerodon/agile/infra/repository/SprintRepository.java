package io.choerodon.agile.infra.repository;

import io.choerodon.agile.infra.dataobject.SprintConvertDTO;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintRepository {
    SprintConvertDTO createSprint(SprintConvertDTO sprintConvertDTO);

    SprintConvertDTO updateSprint(SprintConvertDTO sprintConvertDTO);

    Boolean deleteSprint(SprintConvertDTO sprintConvertDTO);

    void updateSprintNameByBatch(Long programId, List<Long> sprintIds);
}
