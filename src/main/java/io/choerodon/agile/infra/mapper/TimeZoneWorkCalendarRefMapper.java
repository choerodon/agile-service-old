package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/15
 */
public interface TimeZoneWorkCalendarRefMapper extends Mapper<TimeZoneWorkCalendarRefDO> {

    /**
     * 按年份获取今年和下一年的时区工作日期
     *
     * @param organizationId organizationId
     * @param timeZoneId     timeZoneId
     * @param year           year
     * @return TimeZoneWorkCalendarRefDO
     */
    List<TimeZoneWorkCalendarRefDO> queryWithNextYearByYear(@Param("organizationId") Long organizationId,
                                                            @Param("timeZoneId") Long timeZoneId, @Param("year") Integer year);
}
