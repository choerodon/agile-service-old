package io.choerodon.agile.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.WorkCalendarRefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IterativeWorktableValidator;
import io.choerodon.agile.app.assembler.IterativeWorktableAssembler;
import io.choerodon.agile.app.service.IterativeWorktableService;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.mapper.IterativeWorktableMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.convertor.ConvertHelper;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class IterativeWorktableServiceImpl implements IterativeWorktableService {

    private static final String CATEGORY_DONE = "done";

    @Autowired
    private IterativeWorktableMapper iterativeWorktableMapper;

    @Autowired
    private SprintMapper sprintMapper;

    @Autowired
    private IterativeWorktableAssembler iterativeWorktableAssembler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private WorkCalendarRefMapper workCalendarRefMapper;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    @Override
    public List<PriorityDistributeDTO> queryPriorityDistribute(Long projectId, Long sprintId, Long organizationId) {
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDO);
        List<PriorityDistributeDO> priorityDistributeDTOList = iterativeWorktableMapper.queryPriorityDistribute(projectId, sprintId);
        Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        for (PriorityDistributeDO priorityDistributeDO : priorityDistributeDTOList) {
            priorityDistributeDO.setPriorityDTO(priorityMap.get(priorityDistributeDO.getPriorityId()));
            priorityDistributeDO.setCategoryCode(statusMapDTOMap.get(priorityDistributeDO.getStatusId()).getType());
        }
        Map<Long, PriorityDistributeDTO> result = new HashMap<>();
        for (PriorityDistributeDO priorityDistributeDO : priorityDistributeDTOList) {
            Long priorityId = priorityDistributeDO.getPriorityDTO().getId();
            if (result.get(priorityId) == null) {
                PriorityDistributeDTO priorityDistributeDTO = new PriorityDistributeDTO();
                priorityDistributeDTO.setTotalNum(1);
                if (CATEGORY_DONE.equals(priorityDistributeDO.getCategoryCode())) {
                    priorityDistributeDTO.setCompletedNum(1);
                } else {
                    priorityDistributeDTO.setCompletedNum(0);
                }
                priorityDistributeDTO.setPriorityDTO(priorityDistributeDO.getPriorityDTO());
                result.put(priorityId, priorityDistributeDTO);
            } else {
                PriorityDistributeDTO priorityDistributeDTO = result.get(priorityId);
                priorityDistributeDTO.setTotalNum(priorityDistributeDTO.getTotalNum() + 1);
                if (CATEGORY_DONE.equals(priorityDistributeDO.getCategoryCode())) {
                    priorityDistributeDTO.setCompletedNum(priorityDistributeDTO.getCompletedNum() + 1);
                }
                result.put(priorityId, priorityDistributeDTO);
            }
        }
        List<PriorityDistributeDTO> res = new ArrayList<>();
        for (Long key : result.keySet()) {
            res.add(result.get(key));
        }
        Collections.sort(res, Comparator.comparing(PriorityDistributeDTO::getTotalNum));
        Collections.reverse(res);
        return res;
    }

    @Override
    public List<StatusCategoryDTO> queryStatusCategoryDistribute(Long projectId, Long sprintId, Long organizationId) {
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDO);
        List<StatusCategoryDO> statusCategoryDOList = iterativeWorktableMapper.queryStatusCategoryDistribute(projectId, sprintId);
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        for (StatusCategoryDO statusCategoryDO : statusCategoryDOList) {
            statusCategoryDO.setCategoryCode(statusMapDTOMap.get(statusCategoryDO.getStatusId()).getType());
        }
        return ConvertHelper.convertList(statusCategoryDOList, StatusCategoryDTO.class);
    }

    @Override
    public SprintInfoDTO querySprintInfo(Long projectId, Long sprintId, Long organizationId) {
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDO);
        Date actualEndDate = sprintDO.getActualEndDate();
        SprintInfoDTO result = ConvertHelper.convert(sprintDO, SprintInfoDTO.class);
        List<AssigneeIssueDO> assigneeIssueDOList = iterativeWorktableMapper.queryAssigneeInfoBySprintId(projectId, sprintId);
        result.setAssigneeIssueDTOList(iterativeWorktableAssembler.assigneeIssueDOToDTO(assigneeIssueDOList));
        if (actualEndDate == null && result.getEndDate() != null) {
            Date startDate = new Date();
            if (result.getStartDate().after(startDate)) {
                startDate = result.getStartDate();
            }
            result.setDayRemain(dateUtil.getDaysBetweenDifferentDate(startDate, result.getEndDate(),
                    workCalendarRefMapper.queryHolidayBySprintIdAndProjectId(sprintId, projectId),
                    workCalendarRefMapper.queryWorkBySprintIdAndProjectId(sprintId, projectId), organizationId));
        } else {
            result.setDayRemain(dateUtil.getDaysBetweenDifferentDate(result.getStartDate(), result.getEndDate(),
                    workCalendarRefMapper.queryHolidayBySprintIdAndProjectId(sprintId, projectId),
                    workCalendarRefMapper.queryWorkBySprintIdAndProjectId(sprintId, projectId), organizationId));
        }
        result.setDayTotal(dateUtil.getDaysBetweenDifferentDate(result.getStartDate(), result.getEndDate(),
                workCalendarRefMapper.queryHolidayBySprintIdAndProjectId(sprintId, projectId),
                workCalendarRefMapper.queryWorkBySprintIdAndProjectId(sprintId, projectId), organizationId));
        result.setIssueCount(sprintMapper.queryIssueCountInActiveBoard(projectId, sprintId));
        return result;
    }

    @Override
    public List<AssigneeDistributeDTO> queryAssigneeDistribute(Long projectId, Long sprintId) {
        Integer total = iterativeWorktableMapper.queryAssigneeAll(projectId, sprintId);
        List<AssigneeDistributeDTO> assigneeDistributeDTOList = ConvertHelper.convertList(iterativeWorktableMapper.queryAssigneeDistribute(projectId, sprintId, total), AssigneeDistributeDTO.class);
        if (assigneeDistributeDTOList != null && !assigneeDistributeDTOList.isEmpty()) {
            List<Long> userIds = assigneeDistributeDTOList.stream().filter(assigneeDistributeDTO ->
                    assigneeDistributeDTO.getAssigneeId() != null).map(assigneeDistributeDTO -> (assigneeDistributeDTO.getAssigneeId())).collect(Collectors.toList());
            Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(userIds, true);
            assigneeDistributeDTOList.parallelStream().forEach(assigneeDistributeDTO -> {
                if (assigneeDistributeDTO.getAssigneeId() != null && usersMap.get(assigneeDistributeDTO.getAssigneeId()) != null) {
                    assigneeDistributeDTO.setAssigneeName(usersMap.get(assigneeDistributeDTO.getAssigneeId()).getName());
                } else {
                    assigneeDistributeDTO.setAssigneeName("未分配");
                }
            });
        }
        return assigneeDistributeDTOList;
    }

    @Override
    public List<IssueTypeDistributeDTO> queryIssueTypeDistribute(Long projectId, Long sprintId, Long organizationId) {
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        List<IssueTypeDistributeDO> issueTypeDistributeDOList = iterativeWorktableMapper.queryIssueTypeDistribute(projectId, sprintId);
        for (IssueTypeDistributeDO issueTypeDistributeDO : issueTypeDistributeDOList) {
            List<IssueStatus> issueStatuses = issueTypeDistributeDO.getIssueStatus();
            for (IssueStatus issueStatus : issueStatuses) {
                issueStatus.setCategoryCode(statusMapDTOMap.get(issueStatus.getStatusId()).getType());
            }
        }
        return ConvertHelper.convertList(issueTypeDistributeDOList, IssueTypeDistributeDTO.class);
    }
}
