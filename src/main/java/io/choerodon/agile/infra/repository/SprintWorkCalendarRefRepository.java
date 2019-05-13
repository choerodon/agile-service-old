package io.choerodon.agile.infra.repository;

import io.choerodon.agile.infra.dataobject.WorkCalendarRefDO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
public interface SprintWorkCalendarRefRepository {

    /**
     * 创建
     *
     * @param workCalendarRefDO sprintWorkCalendarRefDO
     * @return SprintWorkCalendarRefDO
     */
    WorkCalendarRefDO create(WorkCalendarRefDO workCalendarRefDO);

    /**
     * 删除冲刺工作日历
     *
     * @param projectId  projectId
     * @param calendarId calendarId
     */
    void delete(Long projectId, Long calendarId);
}
