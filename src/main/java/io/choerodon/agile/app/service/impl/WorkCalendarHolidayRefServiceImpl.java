package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.WorkCalendarHolidayRefDTO;
import io.choerodon.agile.app.assembler.WorkCalendarHolidayRefAssembler;
import io.choerodon.agile.app.service.WorkCalendarHolidayRefService;
import io.choerodon.agile.app.service.WorkCalendarService;
import io.choerodon.agile.infra.common.properties.WorkCalendarHolidayProperties;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.factory.WorkCalendarFactory;
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
@Service
@Transactional(rollbackFor = Exception.class)
@EnableConfigurationProperties(WorkCalendarHolidayProperties.class)
public class WorkCalendarHolidayRefServiceImpl implements WorkCalendarHolidayRefService {

    @Autowired
    private WorkCalendarHolidayProperties workCalendarHolidayProperties;
    @Autowired
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;
    @Autowired
    private WorkCalendarHolidayRefAssembler workCalendarHolidayRefAssembler;
    @Autowired
    private WorkCalendarFactory workCalendarFactory;

    @Override
    public void updateWorkCalendarHolidayRefByYear(Integer year) {
        WorkCalendarService workCalendarService = workCalendarFactory.getWorkCalendarHoliday(workCalendarHolidayProperties.getType());
        if (workCalendarService != null) {
            workCalendarService.updateWorkCalendarHolidayRefByYear(year);
        }
    }

    @Override
    public List<WorkCalendarHolidayRefDTO> queryWorkCalendarHolidayRelByYear(Integer year) {
        return workCalendarHolidayRefAssembler.toTargetList(DateUtil.stringDateCompare().
                sortedCopy(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelByYear(year)), WorkCalendarHolidayRefDTO.class);
    }
}
