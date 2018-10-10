package io.choerodon.agile.infra.common.utils;

import com.google.common.collect.Ordering;
import io.choerodon.agile.infra.dataobject.WorkCalendarHolidayRefDO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/31.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DateUtil {

    private DateUtil() {}

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1,start_date date1,start_date
     * @param date2,end_date   date2,end_date
     * @return int
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return days - getWeekendNum(simpleDateFormat.format(date1.getTime()), simpleDateFormat.format(date2.getTime()), format);
    }

    public static int getWeekendNum(String startDate, String endDate, String format) {
        List yearMonthDayList = new ArrayList();
        Date start = null;
        Date stop = null;
        try {
            start = new SimpleDateFormat(format).parse(startDate);
            stop = new SimpleDateFormat(format).parse(endDate);
        } catch (ParseException e) {
            throw new CommonException(e.getMessage());
        }
        if (start.after(stop)) {
            Date tmp = start;
            start = stop;
            stop = tmp;
        }
        Calendar calendarTemp = Calendar.getInstance();
        calendarTemp.setTime(start);
        while (calendarTemp.getTime().getTime() <= stop.getTime()) {
            yearMonthDayList.add(new SimpleDateFormat(format)
                    .format(calendarTemp.getTime()));
            calendarTemp.add(Calendar.DAY_OF_YEAR, 1);
        }
        Collections.sort(yearMonthDayList);
        int num = 0;
        int size = yearMonthDayList.size();
        int week = 0;
        for (int i = 0; i < size; i++) {
            String day = (String) yearMonthDayList.get(i);
            week = getWeek(day, format);
            if (week == 6 || week == 0) {
                num++;
            }
        }
        return num;
    }

    public static int getWeek(String date, String format) {
        Calendar calendarTemp = Calendar.getInstance();
        try {
            calendarTemp.setTime(new SimpleDateFormat(format).parse(date));
        } catch (ParseException e) {
            throw new CommonException(e.getMessage());
        }
        return calendarTemp.get(Calendar.DAY_OF_WEEK) - 1;
    }


    /**
     * 从现有比较器返回一个
     *
     * @return Ordering
     */
    public static Ordering<WorkCalendarHolidayRefDO> stringDateCompare() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return Ordering.from((o1, o2) -> {
            int a = 0;
            try {
                a = sdf.parse(o1.getHoliday()).compareTo(sdf.parse(o2.getHoliday()));
            } catch (ParseException e) {
                throw new CommonException("ParseException{}", e);
            }
            return a;
        });
    }

}
