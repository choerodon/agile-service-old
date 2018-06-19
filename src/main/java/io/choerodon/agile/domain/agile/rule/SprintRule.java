package io.choerodon.agile.domain.agile.rule;

import io.choerodon.agile.api.dto.SprintUpdateDTO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by jian_zhang02@163.com on 2018/5/22.
 */
@Component
public class SprintRule {
    @Autowired
    private SprintMapper sprintMapper;
    private static final String ERROR_COMPLETE_SPRINT = "error.sprint.complete";
    private static final String SPRINT_NOT_FOUND = "error.sprint.notFound";
    private static final String SPRINT_ERROR = "error.sprint.notFoundOrIsClosed";
    private static final String SPRINT_START_CODE = "started";
    private static final String SPRINT_PLANNING_CODE = "sprint_planning";
    private static final String SPRINT_CLOSED_CODE = "closed";
    private static final String SPRINT_DATE_ERROR = "error.sprintDate.nullOrStartAfterEndDate";

    public void judgeExist(Long projectId, Long sprintId) {
        if (sprintId != null && !Objects.equals(sprintId, 0L)) {
            SprintDO sprintDO = new SprintDO();
            sprintDO.setProjectId(projectId);
            sprintDO.setSprintId(sprintId);
            sprintDO = sprintMapper.selectOne(sprintDO);
            if (sprintDO == null || Objects.equals(sprintDO.getStatusCode(), SPRINT_CLOSED_CODE)) {
                throw new CommonException(SPRINT_ERROR);
            }
        }
    }

    public Boolean hasIssue(Long projectId, Long sprintId) {
        return sprintMapper.hasIssue(projectId, sprintId);
    }

    public void judgeCompleteSprint(Long projectId, Long sprintId, Long targetSprintId) {
        judgePlanningExist(projectId, targetSprintId);
        if (!sprintMapper.queryParentsDoneUnfinishedSubtasks(projectId, sprintId).isEmpty()) {
            throw new CommonException(ERROR_COMPLETE_SPRINT);
        }
    }

    private void judgePlanningExist(Long projectId, Long sprintId) {
        if (sprintId != null && !Objects.equals(sprintId, 0L)) {
            SprintDO sprintDO = new SprintDO();
            sprintDO.setProjectId(projectId);
            sprintDO.setStatusCode(SPRINT_PLANNING_CODE);
            sprintDO.setSprintId(sprintId);
            if (sprintMapper.selectOne(sprintDO) == null) {
                throw new CommonException(SPRINT_NOT_FOUND);
            }
        }
    }

    public void checkDate(SprintUpdateDTO sprintUpdateDTO) {
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(sprintUpdateDTO.getProjectId());
        sprintDO.setSprintId(sprintUpdateDTO.getSprintId());
        sprintDO = sprintMapper.selectOne(sprintDO);
        if (sprintDO == null || (Objects.equals(sprintDO.getStatusCode(), SPRINT_START_CODE)
                && (sprintDO.getStartDate() == null || sprintDO.getEndDate() == null || sprintDO.getStartDate().after(sprintDO.getEndDate())))) {
            throw new CommonException(SPRINT_DATE_ERROR);
        }
    }

}
