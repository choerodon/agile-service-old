package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.SprintE;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintRepository {
    SprintE createSprint(SprintE sprintE);

    SprintE updateSprint(SprintE sprintE);

    Boolean deleteSprint(SprintE sprintE);

    void updateSprintNameByBatch(Long programId, List<Long> sprintIds);
}
