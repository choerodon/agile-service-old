package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.SprintWorkCalendarDTO;
import io.choerodon.agile.api.vo.WorkCalendarRefCreateVO;
import io.choerodon.agile.api.vo.WorkCalendarRefVO;
import io.choerodon.agile.infra.dataobject.WorkCalendarRefDTO;

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
     * @return List<WorkCalendarRefVO>
     */
    List<WorkCalendarRefVO> queryProjectWorkCalendarRefs(Long projectId, Integer year);

    /**
     * 创建工作日历
     *
     * @param projectId                      projectId
     * @param sprintId                       sprintId
     * @param workCalendarRefCreateVO workCalendarRefCreateVO
     * @return WorkCalendarRefVO
     */
    WorkCalendarRefVO createWorkCalendarRef(Long projectId, Long sprintId, WorkCalendarRefCreateVO workCalendarRefCreateVO);

    /**
     * 删除工作日历
     *
     * @param projectId  projectId
     * @param calendarId calendarId
     */
    void deleteWorkCalendarRef(Long projectId, Long calendarId);

    WorkCalendarRefDTO create(WorkCalendarRefDTO workCalendarRefDTO);
}
