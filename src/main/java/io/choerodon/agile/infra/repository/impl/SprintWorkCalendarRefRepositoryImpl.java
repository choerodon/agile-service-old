package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.repository.SprintWorkCalendarRefRepository;
import io.choerodon.agile.infra.dataobject.SprintWorkCalendarRefDO;
import io.choerodon.agile.infra.mapper.SprintWorkCalendarRefMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class SprintWorkCalendarRefRepositoryImpl implements SprintWorkCalendarRefRepository {

    private static final String INSERT_ERROR = "error.SprintWorkCalendarRef.create";
    private static final String DELETE_ERROR = "error.SprintWorkCalendarRef.delete";

    @Autowired
    private SprintWorkCalendarRefMapper sprintWorkCalendarRefMapper;

    @Override
    public SprintWorkCalendarRefDO create(SprintWorkCalendarRefDO sprintWorkCalendarRefDO) {
        if (sprintWorkCalendarRefMapper.insert(sprintWorkCalendarRefDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return sprintWorkCalendarRefDO;
    }

    @Override
    public void delete(Long projectId, Long calendarId) {
        SprintWorkCalendarRefDO sprintWorkCalendarRefDO = new SprintWorkCalendarRefDO();
        sprintWorkCalendarRefDO.setProjectId(projectId);
        sprintWorkCalendarRefDO.setCalendarId(calendarId);
        if (sprintWorkCalendarRefMapper.delete(sprintWorkCalendarRefDO) != 1) {
            throw new CommonException(DELETE_ERROR);
        }
    }
}
