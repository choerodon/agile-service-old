package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.SprintUpdateDTO;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/5.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class SprintValidator {

    private static final String SPRINT_NOT_FOUND = "error.sprint.notFound";
    private static final String SPRINT_ERROR = "error.sprint.notFoundOrIsClosed";
    private static final String SPRINT_START_CODE = "started";
    private static final String SPRINT_PLANNING_CODE = "sprint_planning";
    private static final String SPRINT_CLOSED_CODE = "closed";
    private static final String SPRINT_DATE_ERROR = "error.sprintDate.nullOrStartAfterEndDate";

    @Autowired
    private SprintMapper sprintMapper;

    public void checkSprintStartInProgram(SprintE sprintE) {
        Long sprintId = sprintE.getSprintId();
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        if (sprintDO == null) {
            throw new CommonException("error.sprint.get");
        }
        if (sprintDO.getPiId() != null) {
            Date nowDate = new Date();
            if (nowDate.before(sprintE.getEndDate())) {
                sprintE.setStartDate(nowDate);
            }
        }
    }

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

    public void judgeCompleteSprint(Long projectId, Long targetSprintId) {
        judgePlanningExist(projectId, targetSprintId);
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

    public void validatorSprint(Long sprintId, Long projectId) {
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(projectId);
        sprintDO.setSprintId(sprintId);
        sprintDO = sprintMapper.selectOne(sprintDO);
        if (sprintDO == null) {
            throw new CommonException("error.spring.query");
        }
    }

}
