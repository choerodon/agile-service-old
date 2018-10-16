package io.choerodon.agile.domain.agile.entity;

import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/16
 */
public class WorkDayE {

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkDayE)) {
            return false;
        }
        WorkDayE workDayE = (WorkDayE) o;
        return DateUtil.isSameDay(getDate(), workDayE.getDate());
    }

    @Override
    public int hashCode() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return Objects.hash(simpleDateFormat.format(getDate()));
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
