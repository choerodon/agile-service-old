package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public interface TimeZoneWorkCalendarMapper extends Mapper<TimeZoneWorkCalendarDO> {

    /**
     * 查询时区详情
     * @param  organizationId organizationId
     * @return TimeZoneWorkCalendarDO
     */
    TimeZoneWorkCalendarDO queryTimeZoneDetailByOrganizationId(@Param("organizationId") Long organizationId);
}
