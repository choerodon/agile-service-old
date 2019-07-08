package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.SprintWorkCalendarDTO;
import io.choerodon.agile.api.vo.WorkCalendarRefCreateDTO;
import io.choerodon.agile.api.vo.WorkCalendarRefDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2019/4/28
 */
public interface WorkCalendarRefService {
    /**
     * 查询冲刺工作日历设置
     *
     * @param projectId projectId
     * @param year      year
     * @return SprintWorkCalendarRefDTO
     */
    SprintWorkCalendarDTO querySprintWorkCalendarRefs(Long projectId, Integer year);

    /**
     * 查询项目工作日历设置
     *
     * @param projectId projectId
     * @param year      year
     * @return List<WorkCalendarRefDTO>
     */
    List<WorkCalendarRefDTO> queryProjectWorkCalendarRefs(Long projectId, Integer year);

    /**
     * 创建工作日历
     *
     * @param projectId                      projectId
     * @param sprintId                       sprintId
     * @param workCalendarRefCreateDTO workCalendarRefCreateDTO
     * @return WorkCalendarRefDTO
     */
    WorkCalendarRefDTO createWorkCalendarRef(Long projectId, Long sprintId, WorkCalendarRefCreateDTO workCalendarRefCreateDTO);

    /**
     * 删除工作日历
     *
     * @param projectId  projectId
     * @param calendarId calendarId
     */
    void deleteWorkCalendarRef(Long projectId, Long calendarId);
}
