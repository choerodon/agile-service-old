package io.choerodon.agile.api.validator;


import io.choerodon.core.exception.CommonException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/18
 */
public class WorkCalendarValidator {

    private WorkCalendarValidator() {
    }

    public static void checkWorkDayAndStatus(String workDay, Integer status) {
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
        try {
            simpleDateFormat.parse(workDay);
        } catch (ParseException e) {
            throw new CommonException("error.workDay.error");
        }
    }
}
