package io.choerodon.agile.infra.common.utils;

import com.google.common.collect.Ordering;
import io.choerodon.agile.infra.dataobject.WorkCalendarHolidayRefDO;
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    @Autowired
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private DateUtil() {
    }

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
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
        return Ordering.from((o1, o2) -> {
            int a;
            try {
                a = sdf.parse(o1.getHoliday()).compareTo(sdf.parse(o2.getHoliday()));
            } catch (ParseException e) {
                throw new CommonException("ParseException{}", e);
            }
            return a;
        });
    }

    /**
     * 获取时间段内的非工作日(包含周末和国家法定节假日)
     *
     * @param dayOne dayOne
     * @param dayTwo dayTwo
     * @return Date
     */
    public Set<Date> getNonWorkdaysDuring(Date dayOne, Date dayTwo) {
        Set<Integer> year = new HashSet<>();
        Set<Date> dates = new HashSet<>();
        if (dayOne == null || dayTwo == null) {
            return dates;
        } else if (isSameDay(dayOne, dayTwo)) {
            final Date date = dayOne;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dayOne);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dates.add(calendar.getTime());
            }
            Set<WorkCalendarHolidayRefDO> holidays = (new HashSet<>(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelByYear(String.valueOf(calendar.get(Calendar.YEAR)))));
            if (!holidays.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
                holidays.forEach(holiday -> {
                    try {
                        if (isSameDay(date, sdf.parse(holiday.getHoliday()))) {
                            if ("0".equals(holiday.getStatus())) {
                                dates.add(date);
                            } else {
                                dates.remove(date);
                            }
                        }
                    } catch (ParseException e) {
                        LOGGER.warn("ParseException{}", e);
                    }
                });
            }
            return dates;
        } else {
            if (dayOne.after(dayTwo)) {
                Date tmp = dayOne;
                dayOne = dayTwo;
                dayTwo = tmp;
            }
            final Date startDate = dayOne;
            final Date endDate = dayTwo;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            while (calendar.getTime().getTime() <= endDate.getTime()) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    dates.add(calendar.getTime());
                }
                year.add(calendar.get(Calendar.YEAR));
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            if (!dates.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
                Set<WorkCalendarHolidayRefDO> holidays = new HashSet<>();
                year.forEach(y -> holidays.addAll(new HashSet<>(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelByYear(String.valueOf(y)))));
                if (!holidays.isEmpty()) {
                    Set<Date> remove = new HashSet<>(dates.size() << 1);
                    Set<Date> add = new HashSet<>(dates.size() << 1);
                    dates.forEach(date -> holidays.forEach(holiday -> {
                        try {
                            Date holidayDate = sdf.parse(holiday.getHoliday());
                            if (isSameDay(holidayDate, date) && "1".equals(holiday.getStatus())) {
                                remove.add(date);
                            } else if (holidayDate.before(endDate) && holidayDate.after(startDate)) {
                                add.add(holidayDate);
                            }
                        } catch (ParseException e) {
                            LOGGER.warn("ParseException{}", e);
                        }
                    }));
                    dates.addAll(add);
                    dates.removeAll(remove);
                }
            }
            return dates;
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }


}
