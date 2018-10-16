package io.choerodon.agile.domain.agile.entity;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/15
 */
public class TimeZoneWorkCalendarRefE {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private Long calendarId;

    private Long timeZoneId;

    private String workDay;

    private Integer year;

    private Long organizationId;

    private Long objectVersionNumber;

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public TimeZoneWorkCalendarRefE(Long timeZoneId, String workDay, Integer status, Long organizationId) throws ParseException {
        this.timeZoneId = timeZoneId;
        this.workDay = workDay;
        this.status = status;
        this.organizationId = organizationId;
        this.year = initYear(workDay);
    }

    public TimeZoneWorkCalendarRefE() {
    }

    private Integer initYear(String workDay) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        calendar.setTime(sdf.parse(workDay));
        return calendar.get(Calendar.YEAR);
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Long getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(Long timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getWorkDay() {
        return workDay;
    }

    public void setWorkDay(String workDay) {
        this.workDay = workDay;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
