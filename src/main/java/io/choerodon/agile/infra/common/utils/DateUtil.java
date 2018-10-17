package io.choerodon.agile.infra.common.utils;

import com.google.common.collect.Ordering;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDO;
import io.choerodon.agile.infra.dataobject.WorkCalendarHolidayRefDO;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/31.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    @Autowired
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;

    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String PARSE_EXCEPTION = "ParseException{}";

    private DateUtil() {
    }

    public void setTimeZoneWorkCalendarMapper(TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper) {
        this.timeZoneWorkCalendarMapper = timeZoneWorkCalendarMapper;
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
     * @param dayOne         dayOne
     * @param dayTwo         dayTwo
     * @param holiday        要排除的日期
     * @param workday        要加班的日期
     * @param organizationId organizationId
     * @return Integer
     */
    public Integer getDaysBetweenDifferentDate(Date dayOne, Date dayTwo, List<Date> holiday, List<Date> workday, Long organizationId) {
        if (dayOne == null || dayTwo == null) {
            throw new IllegalArgumentException("date can't be null");
        } else if (isSameDay(dayOne, dayTwo)) {
            return 1;
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
            Integer i = 0;
            TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(organizationId);
            i = getWorkDaysInterval(i, timeZoneWorkCalendarDO, startDate, endDate, dates, year);
            handleHolidays(dates, year, startDate, endDate, timeZoneWorkCalendarDO);
            handleExcludedDate(workday, dates);
            handleAddDate(holiday, dates);
            return i - dates.size();
        }
    }

    private Integer getWorkDaysInterval(Integer i, TimeZoneWorkCalendarDO timeZoneWorkCalendarDO, Date startDate, Date endDate, Set<Date> dates, Set<Integer> year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        if (timeZoneWorkCalendarDO != null) {
            while (calendar.getTime().getTime() <= endDate.getTime()) {
                handleSaturdayAndSunday(calendar, dates, timeZoneWorkCalendarDO);
                year.add(calendar.get(Calendar.YEAR));
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                i++;
            }
            return i;
        } else {
            while (calendar.getTime().getTime() <= endDate.getTime()) {
                handleSaturdayAndSundayNoTimeZone(calendar, dates);
                year.add(calendar.get(Calendar.YEAR));
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                i++;
            }
            return i;
        }
    }

    private void handleSaturdayAndSundayNoTimeZone(Calendar calendar, Set<Date> dates) {
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dates.add(calendar.getTime());
        }
    }

    private void handleAddDate(List<Date> addDate, Set<Date> dates) {
        if (addDate != null && !addDate.isEmpty()) {
            dates.addAll(addDate);
            handleDuplicateDate(dates);
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
    public Set<Date> getNonWorkdaysDuring(Date dayOne, Date dayTwo, Long organizationId) {
        if (dayOne == null || dayTwo == null) {
            return new HashSet<>();
        } else if (isSameDay(dayOne, dayTwo)) {
            return handleSameDayWorkDays(dayOne, organizationId);
        } else {
            return handleDifferentDayWorkDays(dayOne, dayTwo, organizationId);
        }
    }

    private Set<Date> handleDifferentDayWorkDays(Date dayOne, Date dayTwo, Long organizationId) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(organizationId);
        Set<Integer> year = new HashSet<>();
        Set<Date> dates = new HashSet<>();
        if (dayOne.after(dayTwo)) {
            Date tmp = dayOne;
            dayOne = dayTwo;
            dayTwo = tmp;
        }
        final Date startDate = dayOne;
        final Date endDate = dayTwo;
        getDaysInterval(startDate, endDate, year, dates, timeZoneWorkCalendarDO);
        handleHolidays(dates, year, startDate, endDate, timeZoneWorkCalendarDO);
        handleTimeZoneWorkCalendarRefRemoveAndAdd(dates, timeZoneWorkCalendarDO, startDate, endDate);
        return dates;
    }

    private void handleTimeZoneWorkCalendarRefRemoveAndAdd(Set<Date> dates, TimeZoneWorkCalendarDO timeZoneWorkCalendarDO, Date startDate, Date endDate) {
        if (timeZoneWorkCalendarDO != null && timeZoneWorkCalendarDO.getTimeZoneWorkCalendarRefDOS() != null
                && !timeZoneWorkCalendarDO.getTimeZoneWorkCalendarRefDOS().isEmpty()) {
            Set<Date> remove = new HashSet<>(dates.size() << 1);
            Set<Date> add = new HashSet<>(dates.size() << 1);
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
            List<TimeZoneWorkCalendarRefDO> timeZoneDate = timeZoneWorkCalendarDO.getTimeZoneWorkCalendarRefDOS();
            if (dates.isEmpty()) {
                handleEmptyDates(dates, timeZoneDate, sdf);
            } else {
                handleNoEmptyDates(dates, timeZoneDate, remove, add, sdf, endDate, startDate);

            }
        }
    }

    private void handleEmptyDates(Set<Date> dates, List<TimeZoneWorkCalendarRefDO> timeZoneDate, SimpleDateFormat sdf) {
        dates.addAll(timeZoneDate.stream().filter(timeZoneWorkCalendarRefDO -> timeZoneWorkCalendarRefDO.getStatus() == 0)
                .map(timeZoneWorkCalendarRefDO -> {
                    Date date = new Date();
                    try {
                        date = sdf.parse(timeZoneWorkCalendarRefDO.getWorkDay());
                    } catch (ParseException e) {
                        LOGGER.warn(PARSE_EXCEPTION, e);
                    }
                    return date;
                }).collect(Collectors.toSet()));
    }

    private void handleNoEmptyDates(Set<Date> dates, List<TimeZoneWorkCalendarRefDO> timeZoneDate, Set<Date> remove, Set<Date> add, SimpleDateFormat sdf, Date endDate, Date startDate) {
        dates.forEach(date -> timeZoneDate.forEach(timeZoneWorkCalendarRefDO -> {
            try {
                Date holidayDate = sdf.parse(timeZoneWorkCalendarRefDO.getWorkDay());
                if (isSameDay(holidayDate, date) && timeZoneWorkCalendarRefDO.getStatus() == 1) {
                    remove.add(date);
                } else if (holidayDate.before(endDate) && holidayDate.after(startDate) && timeZoneWorkCalendarRefDO.getStatus() == 0) {
                    add.add(holidayDate);
                }
            } catch (ParseException e) {
                LOGGER.warn(PARSE_EXCEPTION, e);
            }
        }));
        dates.addAll(add);
        dates.removeAll(remove);
        handleDuplicateDate(dates);
    }


    private void handleHolidays(Set<Date> dates, Set<Integer> year, Date startDate, Date endDate, TimeZoneWorkCalendarDO timeZoneWorkCalendarDO) {
        if (!dates.isEmpty() && timeZoneWorkCalendarDO != null) {
            Set<WorkCalendarHolidayRefDO> holidays = new HashSet<>();
            year.forEach(y -> holidays.addAll(new HashSet<>(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelByYear(String.valueOf(y)))));
            if (timeZoneWorkCalendarDO.getUseHoliday() && !holidays.isEmpty()) {
                handleHolidaysRemoveAndAdd(dates, holidays, startDate, endDate);
            }
        } else if (dates.isEmpty() && timeZoneWorkCalendarDO != null && timeZoneWorkCalendarDO.getUseHoliday()) {
            Set<WorkCalendarHolidayRefDO> holidays = new HashSet<>();
            year.forEach(y -> holidays.addAll(new HashSet<>(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelByYear(String.valueOf(y)))));
            if (!holidays.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
                dates.addAll(holidays.stream().map(holiday -> {
                    Date date = new Date();
                    try {
                        date = sdf.parse(holiday.getHoliday());
                    } catch (ParseException e) {
                        LOGGER.warn(PARSE_EXCEPTION, e);
                    }
                    return date;
                }).filter(holiday -> holiday.before(endDate) && holiday.after(startDate)).collect(Collectors.toSet()));
            }
        }
    }

    public void handleDuplicateDate(Set<Date> dates) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> date = dates.stream().map(simpleDateFormat::format).collect(Collectors.toSet());
        Set<Date> datesSet = date.stream().map(s -> {
            Date d = new Date();
            try {
                d = simpleDateFormat.parse(s);
            } catch (ParseException e) {
                LOGGER.warn(PARSE_EXCEPTION, e);
            }
            return d;
        }).collect(Collectors.toSet());
        dates.clear();
        dates.addAll(datesSet);
    }

    private void handleHolidaysRemoveAndAdd(Set<Date> dates, Set<WorkCalendarHolidayRefDO> holidays, Date startDate, Date endDate) {
        Set<Date> remove = new HashSet<>(dates.size() << 1);
        Set<Date> add = new HashSet<>(dates.size() << 1);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
        dates.forEach(date -> holidays.forEach(holiday -> {
            try {
                Date holidayDate = sdf.parse(holiday.getHoliday());
                if (isSameDay(holidayDate, date) && holiday.getStatus() == 1) {
                    remove.add(date);
                } else if (holidayDate.before(endDate) && holidayDate.after(startDate)) {
                    add.add(holidayDate);
                }
            } catch (ParseException e) {
                LOGGER.warn(PARSE_EXCEPTION, e);
            }
        }));
        dates.addAll(add);
        dates.removeAll(remove);
        handleDuplicateDate(dates);
    }


    private void getDaysInterval(Date startDate, Date endDate, Set<Integer> year, Set<Date> dates, TimeZoneWorkCalendarDO timeZoneWorkCalendarDO) {
        if (timeZoneWorkCalendarDO != null) {
            handleTimeZoneDaysInterval(endDate, startDate, timeZoneWorkCalendarDO, dates, year);
        } else {
            handleDaysInterval(endDate, startDate, dates, year);
        }
    }

    private void handleDaysInterval(Date endDate, Date startDate, Set<Date> dates, Set<Integer> year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (calendar.getTime().getTime() <= endDate.getTime()) {
            handleSaturdayAndSundayNoTimeZone(calendar, dates);
            year.add(calendar.get(Calendar.YEAR));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void handleTimeZoneDaysInterval(Date endDate, Date startDate, TimeZoneWorkCalendarDO timeZoneWorkCalendarDO, Set<Date> dates, Set<Integer> year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (calendar.getTime().getTime() <= endDate.getTime()) {
            handleSaturdayAndSunday(calendar, dates, timeZoneWorkCalendarDO);
            year.add(calendar.get(Calendar.YEAR));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }


    private Set<Date> handleSameDayWorkDays(Date date, Long organizationId) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(organizationId);
        Set<Date> dates = new HashSet<>(1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //处理周六周天设置
        if (timeZoneWorkCalendarDO != null) {
            handleSaturdayAndSunday(calendar, dates, timeZoneWorkCalendarDO);
            //处理节假日设置
            handleSameDayHoliday(calendar, timeZoneWorkCalendarDO, dates, date);
            //处理自定义节假日
            handleSameDayTimeZoneCalendarRef(timeZoneWorkCalendarDO, date, dates);
        } else {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dates.add(calendar.getTime());
            }
        }
        return dates;
    }

    private void handleSameDayTimeZoneCalendarRef(TimeZoneWorkCalendarDO timeZoneWorkCalendarDO, Date date, Set<Date> dates) {
        if (timeZoneWorkCalendarDO.getTimeZoneWorkCalendarRefDOS() != null && !timeZoneWorkCalendarDO.getTimeZoneWorkCalendarRefDOS().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
            timeZoneWorkCalendarDO.getTimeZoneWorkCalendarRefDOS().forEach(timeZoneWorkCalendarRefDO -> {
                try {
                    if (isSameDay(date, sdf.parse(timeZoneWorkCalendarRefDO.getWorkDay()))) {
                        if (timeZoneWorkCalendarRefDO.getStatus() == 1) {
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
    }


    private void handleSameDayHoliday(Calendar calendar, TimeZoneWorkCalendarDO timeZoneWorkCalendarDO, Set<Date> dates, Date date) {
        if (timeZoneWorkCalendarDO.getUseHoliday()) {
            Set<WorkCalendarHolidayRefDO> holidays = (new HashSet<>(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelByYear(String.valueOf(calendar.get(Calendar.YEAR)))));
            if (!holidays.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
                    for (WorkCalendarHolidayRefDO holiday : holidays) {
                        handleSameDayAddAndRemove(sdf, date, holiday, dates);
                    }
                } catch (ParseException e) {
                    LOGGER.warn(PARSE_EXCEPTION, e);
                }
            }
        }
    }

    private void handleSameDayAddAndRemove(SimpleDateFormat sdf, Date date, WorkCalendarHolidayRefDO holiday, Set<Date> dates) throws ParseException {
        if (isSameDay(date, sdf.parse(holiday.getHoliday()))) {
            if (holiday.getStatus() == 0) {
                dates.add(date);
            } else {
                dates.remove(date);
            }
        }
    }

    private void handleSaturdayAndSunday(Calendar calendar, Set<Date> dates, TimeZoneWorkCalendarDO timeZoneWorkCalendarDO) {
        Boolean condition = (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && !timeZoneWorkCalendarDO.getSaturdayWork())
                || (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && !timeZoneWorkCalendarDO.getSundayWork());
        if (condition) {
            dates.add(calendar.getTime());
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
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }


}
