package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.infra.dataobject.SprintDO;

import java.util.List;
import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintService {
    SprintDetailDTO createSprint(Long projectId);

    SprintDetailDTO updateSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO);

    Boolean deleteSprint(Long projectId, Long sprintId);

    Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap);

    List<SprintNameDTO> queryNameByProjectId(Long projectId);

    SprintDetailDTO startSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO);

    Boolean completeSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO);

    SprintCompleteMessageDTO queryCompleteMessageBySprintId(Long projectId, Long sprintId);

    SprintDO getActiveSprint(Long projectId);

    SprintDetailDTO querySprintById(Long projectId, Long sprintId);
}
