package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.repository.SprintWorkCalendarRefRepository;
import io.choerodon.agile.infra.dataobject.WorkCalendarRefDTO;
import io.choerodon.agile.infra.mapper.WorkCalendarRefMapper;
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
    private WorkCalendarRefMapper workCalendarRefMapper;

    @Override
    public WorkCalendarRefDTO create(WorkCalendarRefDTO workCalendarRefDTO) {
        if (workCalendarRefMapper.insert(workCalendarRefDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return workCalendarRefDTO;
    }

    @Override
    public void delete(Long projectId, Long calendarId) {
        WorkCalendarRefDTO workCalendarRefDTO = new WorkCalendarRefDTO();
        workCalendarRefDTO.setProjectId(projectId);
        workCalendarRefDTO.setCalendarId(calendarId);
        if (workCalendarRefMapper.delete(workCalendarRefDTO) != 1) {
            throw new CommonException(DELETE_ERROR);
        }
    }
}
