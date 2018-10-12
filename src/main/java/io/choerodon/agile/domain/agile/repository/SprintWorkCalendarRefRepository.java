package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.infra.dataobject.SprintWorkCalendarRefDO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
public interface SprintWorkCalendarRefRepository {

    /**
     * 创建
     * @param  sprintWorkCalendarRefDO sprintWorkCalendarRefDO
     * @return SprintWorkCalendarRefDO
     */
    SprintWorkCalendarRefDO create(SprintWorkCalendarRefDO sprintWorkCalendarRefDO);

}
