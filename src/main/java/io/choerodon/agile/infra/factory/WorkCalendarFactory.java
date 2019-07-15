package io.choerodon.agile.infra.factory;

import io.choerodon.agile.app.service.WorkCalendarService;
import io.choerodon.agile.app.service.impl.JuheWorkCalendarServiceImpl;
import io.choerodon.agile.infra.common.properties.WorkCalendarHolidayProperties;
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
@Component
public class WorkCalendarFactory {

    @Autowired
    private WorkCalendarHolidayProperties workCalendarHolidayProperties;

    @Autowired
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;

//    @Autowired
//    private WorkCalendarHolidayRefRepository workCalendarHolidayRefRepository;

    private static final String JU_HE = "juhe";

    public WorkCalendarService getWorkCalendarHoliday(String type) {
        if (type == null) {
            return null;
        }
        if (type.equals(JU_HE)) {
            return new JuheWorkCalendarServiceImpl(workCalendarHolidayProperties, workCalendarHolidayRefMapper);
        }
        return null;
    }
}
