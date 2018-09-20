package io.choerodon.agile.app.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.IterativeWorktableValidator;
import io.choerodon.agile.app.assembler.IterativeWorktableAssembler;
import io.choerodon.agile.app.service.IterativeWorktableService;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.dataobject.AssigneeIssueDO;
import io.choerodon.agile.infra.dataobject.PriorityDistributeDO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.mapper.IterativeWorktableMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.convertor.ConvertHelper;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class IterativeWorktableServiceImpl implements IterativeWorktableService {

    private static final String PRIORITY_HIGH = "high";
    private static final String PRIORITY_MEDIUM = "medium";
    private static final String PRIORITY_LOW = "low";
    private static final String CATEGORY_DONE = "done";

    @Autowired
    private IterativeWorktableMapper iterativeWorktableMapper;

    @Autowired
    private SprintMapper sprintMapper;

    @Autowired
    private IterativeWorktableAssembler iterativeWorktableAssembler;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<PriorityDistributeDTO> queryPriorityDistribute(Long projectId, Long sprintId) {
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDO);
        Integer highCompletedNum = 0, highTotalNum = 0;
        Integer mediumCompletedNum = 0, mediumTotalNum = 0;
        Integer lowCompletedNum = 0, lowTotalNum = 0;
        List<PriorityDistributeDO> priorityDistributeDTOList = iterativeWorktableMapper.queryPriorityDistribute(projectId, sprintId);
        for (PriorityDistributeDO priorityDistributeDO : priorityDistributeDTOList) {
            switch (priorityDistributeDO.getPriorityCode()) {
                case PRIORITY_HIGH:
                    highTotalNum += 1;
                    if (CATEGORY_DONE.equals(priorityDistributeDO.getCategoryCode())) {
                        highCompletedNum += 1;
                    }
                    break;
                case PRIORITY_MEDIUM:
                    mediumTotalNum += 1;
                    if (CATEGORY_DONE.equals(priorityDistributeDO.getCategoryCode())) {
                        mediumCompletedNum += 1;
                    }
                    break;
                case PRIORITY_LOW:
                    lowTotalNum += 1;
                    if (CATEGORY_DONE.equals(priorityDistributeDO.getCategoryCode())) {
                        lowCompletedNum += 1;
                    }
                    break;
                default:
                    break;
            }
        }
        List<PriorityDistributeDTO> result = new ArrayList<>();
        result.add(new PriorityDistributeDTO(PRIORITY_HIGH, highCompletedNum, highTotalNum));
        result.add(new PriorityDistributeDTO(PRIORITY_MEDIUM, mediumCompletedNum, mediumTotalNum));
        result.add(new PriorityDistributeDTO(PRIORITY_LOW, lowCompletedNum, lowTotalNum));
        return result;
    }

    @Override
    public List<StatusCategoryDTO> queryStatusCategoryDistribute(Long projectId, Long sprintId) {
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDO);
        return ConvertHelper.convertList(iterativeWorktableMapper.queryStatusCategoryDistribute(projectId, sprintId), StatusCategoryDTO.class);
    }

    @Override
    public SprintInfoDTO querySprintInfo(Long projectId, Long sprintId) {
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        IterativeWorktableValidator.checkSprintExist(sprintDO);
        SprintInfoDTO result = ConvertHelper.convert(sprintDO, SprintInfoDTO.class);
        List<AssigneeIssueDO> assigneeIssueDOList = iterativeWorktableMapper.queryAssigneeInfoBySprintId(projectId, sprintId);
        result.setAssigneeIssueDTOList(iterativeWorktableAssembler.assigneeIssueDOToDTO(assigneeIssueDOList));
        if (result.getEndDate() != null) {
            result.setDayRemain(DateUtil.differentDaysByMillisecond(new Date(), result.getEndDate()));
        }
        if (result.getStartDate() != null && result.getEndDate() != null) {
            result.setDayTotal(DateUtil.differentDaysByMillisecond(result.getStartDate(), result.getEndDate()));
        }
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
    public List<IssueTypeDistributeDTO> queryIssueTypeDistribute(Long projectId, Long sprintId) {
        return ConvertHelper.convertList(iterativeWorktableMapper.queryIssueTypeDistribute(projectId, sprintId), IssueTypeDistributeDTO.class);
    }
}
