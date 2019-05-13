package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.repository.WorkCalendarHolidayRefRepository;
import io.choerodon.agile.infra.dataobject.WorkCalendarHolidayRefDO;
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
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
public class WorkCalendarHolidayRefRepositoryImpl implements WorkCalendarHolidayRefRepository {

    private static final String INSERT_ERROR = "error.WorkCalendarHolidayRef.create";

    @Autowired
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;

    @Override
    public WorkCalendarHolidayRefDO create(WorkCalendarHolidayRefDO workCalendarHolidayRefDO) {
        if (workCalendarHolidayRefMapper.insert(workCalendarHolidayRefDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return workCalendarHolidayRefDO;
    }
}
