package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.SprintWorkCalendarDTO;
import io.choerodon.agile.api.dto.WorkCalendarRefCreateDTO;
import io.choerodon.agile.api.dto.WorkCalendarRefDTO;
import io.choerodon.agile.api.validator.WorkCalendarValidator;
import io.choerodon.agile.app.assembler.SprintCreateAssembler;
import io.choerodon.agile.app.assembler.SprintSearchAssembler;
import io.choerodon.agile.app.service.WorkCalendarRefService;
import io.choerodon.agile.infra.repository.SprintRepository;
import io.choerodon.agile.infra.repository.SprintWorkCalendarRefRepository;
import io.choerodon.agile.infra.dataobject.SprintSearchDO;
import io.choerodon.agile.infra.dataobject.WorkCalendarRefDO;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.agile.infra.mapper.WorkCalendarRefMapper;
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

    @Autowired
    private SprintRepository sprintRepository;
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
    public SprintWorkCalendarDTO querySprintWorkCalendarRefs(Long projectId, Integer year) {
        SprintSearchDO sprintSearchDO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (sprintSearchDO != null) {
            SprintWorkCalendarDTO sprintWorkCalendarDTO = sprintSearchAssembler.toTarget(sprintSearchDO, SprintWorkCalendarDTO.class);
            sprintWorkCalendarDTO.setWorkCalendarRefDTOS(sprintCreateAssembler.toTargetList(workCalendarRefMapper.queryWithNextYearByYear(projectId, sprintWorkCalendarDTO.getSprintId(), year), WorkCalendarRefDTO.class));
            return sprintWorkCalendarDTO;
        } else {
            return new SprintWorkCalendarDTO();
        }
    }

    @Override
    public List<WorkCalendarRefDTO> queryProjectWorkCalendarRefs(Long projectId, Integer year) {
        return sprintCreateAssembler.toTargetList(workCalendarRefMapper.queryWithNextYearByYear(projectId, null, year), WorkCalendarRefDTO.class);
    }

    @Override
    public WorkCalendarRefDTO createWorkCalendarRef(Long projectId, Long sprintId, WorkCalendarRefCreateDTO workCalendarRefCreateDTO) {
        WorkCalendarRefDO workCalendarRefDO = sprintCreateAssembler.toTarget(workCalendarRefCreateDTO, WorkCalendarRefDO.class);
        workCalendarRefDO.setProjectId(projectId);
        workCalendarRefDO.setSprintId(sprintId);
        workCalendarRefDO.setYear(WorkCalendarValidator.checkWorkDayAndStatus(workCalendarRefDO.getWorkDay(), workCalendarRefDO.getStatus()));
        return sprintSearchAssembler.toTarget(sprintWorkCalendarRefRepository.create(workCalendarRefDO), WorkCalendarRefDTO.class);
    }

    @Override
    public void deleteWorkCalendarRef(Long projectId, Long calendarId) {
        sprintWorkCalendarRefRepository.delete(projectId, calendarId);
    }
}
