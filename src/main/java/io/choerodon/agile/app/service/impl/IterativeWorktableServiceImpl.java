package io.choerodon.agile.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.WorkCalendarRefMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IterativeWorktableValidator;
import io.choerodon.agile.app.assembler.IterativeWorktableAssembler;
import io.choerodon.agile.app.service.IterativeWorktableService;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.utils.DateUtil;
import io.choerodon.agile.infra.mapper.IterativeWorktableMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;

import javax.annotation.PostConstruct;

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
    private UserService userService;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private WorkCalendarRefMapper workCalendarRefMapper;

    @Autowired
    private IssueFeignClient issueFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<PriorityDistributeVO> queryPriorityDistribute(Long projectId, Long sprintId, Long organizationId) {
        SprintDTO sprintDTO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDTO);
        List<PriorityDistributeDTO> priorityDistributeDTOList = iterativeWorktableMapper.queryPriorityDistribute(projectId, sprintId);
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, StatusMapVO> statusMapDTOMap = issueFeignClient.queryAllStatusMap(organizationId).getBody();
        for (PriorityDistributeDTO priorityDistributeDTO : priorityDistributeDTOList) {
            priorityDistributeDTO.setPriorityVO(priorityMap.get(priorityDistributeDTO.getPriorityId()));
            priorityDistributeDTO.setCategoryCode(statusMapDTOMap.get(priorityDistributeDTO.getStatusId()).getType());
        }
        Map<Long, PriorityDistributeVO> result = new HashMap<>();
        for (PriorityDistributeDTO priorityDistributeDTO : priorityDistributeDTOList) {
            Long priorityId = priorityDistributeDTO.getPriorityVO().getId();
            if (result.get(priorityId) == null) {
                PriorityDistributeVO priorityDistributeVO = new PriorityDistributeVO();
                priorityDistributeVO.setTotalNum(1);
                if (CATEGORY_DONE.equals(priorityDistributeDTO.getCategoryCode())) {
                    priorityDistributeVO.setCompletedNum(1);
                } else {
                    priorityDistributeVO.setCompletedNum(0);
                }
                priorityDistributeVO.setPriorityVO(priorityDistributeDTO.getPriorityVO());
                result.put(priorityId, priorityDistributeVO);
            } else {
                PriorityDistributeVO priorityDistributeVO = result.get(priorityId);
                priorityDistributeVO.setTotalNum(priorityDistributeVO.getTotalNum() + 1);
                if (CATEGORY_DONE.equals(priorityDistributeDTO.getCategoryCode())) {
                    priorityDistributeVO.setCompletedNum(priorityDistributeVO.getCompletedNum() + 1);
                }
                result.put(priorityId, priorityDistributeVO);
            }
        }
        List<PriorityDistributeVO> res = new ArrayList<>();
        for (Long key : result.keySet()) {
            res.add(result.get(key));
        }
        Collections.sort(res, Comparator.comparing(PriorityDistributeVO::getTotalNum));
        Collections.reverse(res);
        return res;
    }

    @Override
    public List<StatusCategoryVO> queryStatusCategoryDistribute(Long projectId, Long sprintId, Long organizationId) {
        SprintDTO sprintDTO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDTO);
        List<StatusCategoryDTO> statusCategoryDTOList = iterativeWorktableMapper.queryStatusCategoryDistribute(projectId, sprintId);
        Map<Long, StatusMapVO> statusMapDTOMap = issueFeignClient.queryAllStatusMap(organizationId).getBody();
        for (StatusCategoryDTO statusCategoryDTO : statusCategoryDTOList) {
            statusCategoryDTO.setCategoryCode(statusMapDTOMap.get(statusCategoryDTO.getStatusId()).getType());
        }
        return modelMapper.map(statusCategoryDTOList, new TypeToken<List<StatusCategoryVO>>(){}.getType());
    }

    @Override
    public SprintInfoVO querySprintInfo(Long projectId, Long sprintId, Long organizationId) {
        SprintDTO sprintDTO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDTO);
        Date actualEndDate = sprintDTO.getActualEndDate();
        SprintInfoVO result = modelMapper.map(sprintDTO, SprintInfoVO.class);
        List<AssigneeIssueDTO> assigneeIssueDTOList = iterativeWorktableMapper.queryAssigneeInfoBySprintId(projectId, sprintId);
        result.setAssigneeIssueVOList(iterativeWorktableAssembler.assigneeIssueDTOToVO(assigneeIssueDTOList));
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
    public List<AssigneeDistributeVO> queryAssigneeDistribute(Long projectId, Long sprintId) {
        Integer total = iterativeWorktableMapper.queryAssigneeAll(projectId, sprintId);
        List<AssigneeDistributeVO> assigneeDistributeVOList = modelMapper.map(iterativeWorktableMapper.queryAssigneeDistribute(projectId, sprintId, total), new TypeToken<List<AssigneeDistributeVO>>(){}.getType());
        if (assigneeDistributeVOList != null && !assigneeDistributeVOList.isEmpty()) {
            List<Long> userIds = assigneeDistributeVOList.stream().filter(assigneeDistributeVO ->
                    assigneeDistributeVO.getAssigneeId() != null).map(assigneeDistributeVO -> (assigneeDistributeVO.getAssigneeId())).collect(Collectors.toList());
            Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(userIds, true);
            assigneeDistributeVOList.parallelStream().forEach(assigneeDistributeVO -> {
                if (assigneeDistributeVO.getAssigneeId() != null && usersMap.get(assigneeDistributeVO.getAssigneeId()) != null) {
                    assigneeDistributeVO.setAssigneeName(usersMap.get(assigneeDistributeVO.getAssigneeId()).getName());
                } else {
                    assigneeDistributeVO.setAssigneeName("未分配");
                }
            });
        }
        return assigneeDistributeVOList;
    }

    @Override
    public List<IssueTypeDistributeVO> queryIssueTypeDistribute(Long projectId, Long sprintId, Long organizationId) {
        Map<Long, StatusMapVO> statusMapVOMap = issueFeignClient.queryAllStatusMap(organizationId).getBody();
        List<IssueTypeDistributeDTO> issueTypeDistributeDTOList = iterativeWorktableMapper.queryIssueTypeDistribute(projectId, sprintId);
        for (IssueTypeDistributeDTO issueTypeDistributeDTO : issueTypeDistributeDTOList) {
            List<IssueStatus> issueStatuses = issueTypeDistributeDTO.getIssueStatus();
            for (IssueStatus issueStatus : issueStatuses) {
                issueStatus.setCategoryCode(statusMapVOMap.get(issueStatus.getStatusId()).getType());
            }
        }
        return modelMapper.map(issueTypeDistributeDTOList, new TypeToken<List<IssueTypeDistributeVO>>(){}.getType());
    }
}
