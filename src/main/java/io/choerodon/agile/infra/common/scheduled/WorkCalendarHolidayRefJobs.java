package io.choerodon.agile.infra.common.scheduled;


import io.choerodon.agile.app.service.WorkCalendarService;
import io.choerodon.agile.infra.common.properties.WorkCalendarHolidayProperties;
import io.choerodon.agile.infra.factory.WorkCalendarFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/8
 */
@Component
@Transactional(rollbackFor = Exception.class)
@ConditionalOnProperty(prefix = "workCalendarHoliday", name = "enabled", havingValue = "true")
public class WorkCalendarHolidayRefJobs {

    private WorkCalendarHolidayProperties workCalendarHolidayProperties;

    @Autowired
    private WorkCalendarFactory workCalendarFactory;

    public WorkCalendarHolidayRefJobs(WorkCalendarHolidayProperties workCalendarHolidayProperties) {
        this.workCalendarHolidayProperties = workCalendarHolidayProperties;
    }

    @Scheduled(cron = "${workCalendarHoliday.cron}")
    public void updateWorkCalendarHolidayRef() {
        WorkCalendarService workCalendarService = workCalendarFactory.getWorkCalendarHoliday(workCalendarHolidayProperties.getType());
        if (workCalendarService != null) {
            workCalendarService.updateWorkCalendarHolidayRef();
        }
    }
}
