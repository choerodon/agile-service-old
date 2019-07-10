package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public interface TimeZoneWorkCalendarMapper extends Mapper<TimeZoneWorkCalendarDTO> {

    /**
     * 查询时区详情
     * @param  organizationId organizationId
     * @return TimeZoneWorkCalendarDTO
     */
    TimeZoneWorkCalendarDTO queryTimeZoneDetailByOrganizationId(@Param("organizationId") Long organizationId);
}
