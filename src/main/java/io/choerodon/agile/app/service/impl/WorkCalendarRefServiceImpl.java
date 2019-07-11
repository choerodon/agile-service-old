package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.SprintWorkCalendarVO;
import io.choerodon.agile.api.vo.WorkCalendarRefCreateVO;
import io.choerodon.agile.api.vo.WorkCalendarRefVO;
import io.choerodon.agile.api.validator.WorkCalendarValidator;
import io.choerodon.agile.app.assembler.SprintCreateAssembler;
import io.choerodon.agile.app.assembler.SprintSearchAssembler;
import io.choerodon.agile.app.service.WorkCalendarRefService;
import io.choerodon.agile.infra.repository.SprintWorkCalendarRefRepository;
import io.choerodon.agile.infra.dataobject.SprintSearchDTO;
import io.choerodon.agile.infra.dataobject.WorkCalendarRefDTO;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.agile.infra.mapper.WorkCalendarRefMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkCalendarRefServiceImpl implements WorkCalendarRefService {

    private static final String INSERT_ERROR = "error.SprintWorkCalendarRef.create";
    private static final String DELETE_ERROR = "error.SprintWorkCalendarRef.delete";

    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private SprintCreateAssembler sprintCreateAssembler;
    @Autowired
    private SprintSearchAssembler sprintSearchAssembler;
    @Autowired
    private WorkCalendarRefMapper workCalendarRefMapper;
    @Autowired
    private SprintWorkCalendarRefRepository sprintWorkCalendarRefRepository;

    @Override
    public SprintWorkCalendarVO querySprintWorkCalendarRefs(Long projectId, Integer year) {
        SprintSearchDTO sprintSearchDTO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (sprintSearchDTO != null) {
            SprintWorkCalendarVO sprintWorkCalendarVO = sprintSearchAssembler.toTarget(sprintSearchDTO, SprintWorkCalendarVO.class);
            sprintWorkCalendarVO.setWorkCalendarRefVOS(sprintCreateAssembler.toTargetList(workCalendarRefMapper.queryWithNextYearByYear(projectId, sprintWorkCalendarVO.getSprintId(), year), WorkCalendarRefVO.class));
            return sprintWorkCalendarVO;
        } else {
            return new SprintWorkCalendarVO();
        }
    }

    @Override
    public List<WorkCalendarRefVO> queryProjectWorkCalendarRefs(Long projectId, Integer year) {
        return sprintCreateAssembler.toTargetList(workCalendarRefMapper.queryWithNextYearByYear(projectId, null, year), WorkCalendarRefVO.class);
    }

    @Override
    public WorkCalendarRefVO createWorkCalendarRef(Long projectId, Long sprintId, WorkCalendarRefCreateVO workCalendarRefCreateVO) {
        WorkCalendarRefDTO workCalendarRefDTO = sprintCreateAssembler.toTarget(workCalendarRefCreateVO, WorkCalendarRefDTO.class);
        workCalendarRefDTO.setProjectId(projectId);
        workCalendarRefDTO.setSprintId(sprintId);
        workCalendarRefDTO.setYear(WorkCalendarValidator.checkWorkDayAndStatus(workCalendarRefDTO.getWorkDay(), workCalendarRefDTO.getStatus()));
        return sprintSearchAssembler.toTarget(create(workCalendarRefDTO), WorkCalendarRefVO.class);
    }

    @Override
    public void deleteWorkCalendarRef(Long projectId, Long calendarId) {
//        sprintWorkCalendarRefRepository.delete(projectId, calendarId);
        WorkCalendarRefDTO workCalendarRefDTO = new WorkCalendarRefDTO();
        workCalendarRefDTO.setProjectId(projectId);
        workCalendarRefDTO.setCalendarId(calendarId);
        if (workCalendarRefMapper.delete(workCalendarRefDTO) != 1) {
            throw new CommonException(DELETE_ERROR);
        }
    }

    @Override
    public WorkCalendarRefDTO create(WorkCalendarRefDTO workCalendarRefDTO) {
        if (workCalendarRefMapper.insert(workCalendarRefDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return workCalendarRefDTO;
    }
}
