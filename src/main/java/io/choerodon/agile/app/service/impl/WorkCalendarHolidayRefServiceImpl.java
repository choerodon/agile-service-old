package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.WorkCalendarHolidayRefDTO;
import io.choerodon.agile.app.assembler.WorkCalendarHolidayRefAssembler;
import io.choerodon.agile.app.service.WorkCalendarHolidayRefService;
import io.choerodon.agile.app.service.WorkCalendarService;
import io.choerodon.agile.infra.common.properties.WorkCalendarHolidayProperties;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.dataobject.WorkCalendarHolidayRefDO;
import io.choerodon.agile.infra.factory.WorkCalendarFactory;
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkCalendarHolidayRefServiceImpl.class);
    private static final String PARSE_EXCEPTION = "ParseException{}";

    @Override
    public void updateWorkCalendarHolidayRefByYear(Integer year) {
        WorkCalendarService workCalendarService = workCalendarFactory.getWorkCalendarHoliday(workCalendarHolidayProperties.getType());
        if (workCalendarService != null) {
            workCalendarService.updateWorkCalendarHolidayRefByYear(year);
        }
    }

    @Override
    public List<WorkCalendarHolidayRefDTO> queryWorkCalendarHolidayRelByYear(Integer year) {
        return formatAndSortToDTO(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelWithNextYearByYear(year));

    }

    @Override
    public List<WorkCalendarHolidayRefDTO> queryByYearIncludeLastAndNext(Integer year) {
        return formatAndSortToDTO(workCalendarHolidayRefMapper.queryByYearIncludeLastAndNext(year));
    }

    private List<WorkCalendarHolidayRefDTO> formatAndSortToDTO(List<WorkCalendarHolidayRefDO> workCalendarHolidayRefDOS) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        workCalendarHolidayRefDOS.forEach(workCalendarHolidayRefDO -> {
            try {
                workCalendarHolidayRefDO.setHoliday(simpleDateFormat.format(simpleDateFormat.parse(workCalendarHolidayRefDO.getHoliday())));
            } catch (ParseException e) {
                LOGGER.warn(PARSE_EXCEPTION, e);
            }
        });
        return workCalendarHolidayRefAssembler.toTargetList(DateUtil.stringDateCompare().
                sortedCopy(workCalendarHolidayRefDOS), WorkCalendarHolidayRefDTO.class);
    }
}
