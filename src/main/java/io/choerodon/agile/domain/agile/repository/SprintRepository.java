package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.SprintE;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintRepository {
    SprintE createSprint(SprintE sprintE);

    SprintE updateSprint(SprintE sprintE);

    Boolean deleteSprint(SprintE sprintE);

    void deleteByPiBatch(Long projectId, Long piId);
}
