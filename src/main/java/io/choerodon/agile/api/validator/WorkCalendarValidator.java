package io.choerodon.agile.api.validator;


import io.choerodon.core.exception.CommonException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/18
 */
public class WorkCalendarValidator {

    private WorkCalendarValidator() {
    }

    public static Integer checkWorkDayAndStatus(String workDay, Integer status) {
        if (workDay == null) {
            throw new CommonException("error.workDay.null");
        }
        if (status == null) {
            throw new CommonException("error.status.null");
        }
        if (!(status == 0 || status == 1)) {
            throw new CommonException("error.status.error");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = simpleDateFormat.parse(workDay);
        } catch (ParseException e) {
            throw new CommonException("error.workDay.error");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}
