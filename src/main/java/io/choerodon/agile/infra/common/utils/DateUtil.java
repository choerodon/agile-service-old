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
    private static final String PARSE_EXCEPTION = "ParseException{}";

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
                throw new CommonException(PARSE_EXCEPTION, e);
            }
            return a;
        });
    }

    /**
     * 获取2个时间中的工作天数。排除周末和国家法定节假日
     *
     * @param dayOne       dayOne
     * @param dayTwo       dayTwo
     * @param excludedDate 要排除的日期
     * @return Integer
     */
    public Integer getDaysBetweenDifferentDate(Date dayOne, Date dayTwo, List<Date> excludedDate) {
        if (dayOne == null || dayTwo == null) {
            throw new IllegalArgumentException("date can't be null");
        } else if (isSameDay(dayOne, dayTwo)) {
            return 0;
        } else {
            Set<Integer> year = new HashSet<>();
            Set<Date> dates = new HashSet<>();
            if (dayOne.after(dayTwo)) {
                Date tmp = dayOne;
                dayOne = dayTwo;
                dayTwo = tmp;
            }
            final Date startDate = dayOne;
            final Date endDate = dayTwo;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int i = 0;
            while (calendar.getTime().getTime() <= endDate.getTime()) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    dates.add(calendar.getTime());
                }
                year.add(calendar.get(Calendar.YEAR));
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                i++;
            }
            handleHolidays(dates, year, startDate, endDate);
            handleExcludedDate(excludedDate, dates);
            return i - dates.size();
        }
    }

    private void handleExcludedDate(List<Date> excludedDate, Set<Date> dates) {
        if (excludedDate != null && !excludedDate.isEmpty()) {
            Set<Date> remove = new HashSet<>(dates.size());
            dates.forEach(date -> excludedDate.forEach(d -> {
                if (isSameDay(d, date)) {
                    remove.add(date);
                }
            }));
            dates.removeAll(remove);
        }
    }

    /**
     * 获取时间段内的非工作日(包含周末和国家法定节假日)
     *
     * @param dayOne dayOne
     * @param dayTwo dayTwo
     * @return Date
     */
    public Set<Date> getNonWorkdaysDuring(Date dayOne, Date dayTwo) {
        if (dayOne == null || dayTwo == null) {
            return new HashSet<>();
        } else if (isSameDay(dayOne, dayTwo)) {
            return handleSameDayWorkDays(dayOne);
        } else {
            return handleDifferentDayWorkDays(dayOne, dayTwo);
        }
    }

    private Set<Date> handleDifferentDayWorkDays(Date dayOne, Date dayTwo) {
        Set<Integer> year = new HashSet<>();
        Set<Date> dates = new HashSet<>();
        if (dayOne.after(dayTwo)) {
            Date tmp = dayOne;
            dayOne = dayTwo;
            dayTwo = tmp;
        }
        final Date startDate = dayOne;
        final Date endDate = dayTwo;
        getDaysInterval(startDate, endDate, year, dates);
        handleHolidays(dates, year, startDate, endDate);
        return dates;
    }

    private void handleHolidays(Set<Date> dates, Set<Integer> year, Date startDate, Date endDate) {
        if (!dates.isEmpty()) {
            Set<WorkCalendarHolidayRefDO> holidays = new HashSet<>();
            year.forEach(y -> holidays.addAll(new HashSet<>(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelByYear(String.valueOf(y)))));
            if (!holidays.isEmpty()) {
                Set<Date> remove = new HashSet<>(dates.size() << 1);
                Set<Date> add = new HashSet<>(dates.size() << 1);
                Set<Date> adds = new HashSet<>(dates.size() << 1);
                handleHolidaysRemoveAndAdd(remove, add, dates, holidays, startDate, endDate);
                add.forEach(date -> dates.forEach(d -> {
                    if (!isSameDay(date, d)) {
                        adds.add(d);
                    }
                }));
                dates.addAll(adds);
                dates.removeAll(remove);
            }
        }
    }

    private void handleHolidaysRemoveAndAdd(Set<Date> remove, Set<Date> add, Set<Date> dates, Set<WorkCalendarHolidayRefDO> holidays, Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
        dates.forEach(date -> holidays.forEach(holiday -> {
            try {
                Date holidayDate = sdf.parse(holiday.getHoliday());
                if (isSameDay(holidayDate, date) && "1".equals(holiday.getStatus())) {
                    remove.add(date);
                } else if (holidayDate.before(endDate) && holidayDate.after(startDate)) {
                    add.add(holidayDate);
                }
            } catch (ParseException e) {
                LOGGER.warn(PARSE_EXCEPTION, e);
            }
        }));
    }


    private void getDaysInterval(Date startDate, Date endDate, Set<Integer> year, Set<Date> dates) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (calendar.getTime().getTime() <= endDate.getTime()) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dates.add(calendar.getTime());
            }
            year.add(calendar.get(Calendar.YEAR));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private Set<Date> handleSameDayWorkDays(Date date) {
        Set<Date> dates = new HashSet<>(1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
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
                    LOGGER.warn(PARSE_EXCEPTION, e);
                }
            });
        }
        return dates;
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
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }


}
