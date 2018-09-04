package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.PriorityDistributeDTO;
import io.choerodon.agile.api.dto.SprintInfoDTO;
import io.choerodon.agile.api.dto.StatusCategoryDTO;
import io.choerodon.agile.app.assembler.IterativeWorktableAssembler;
import io.choerodon.agile.app.service.IterativeWorktableService;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.dataobject.AssigneeIssueDO;
import io.choerodon.agile.infra.dataobject.PriorityDistributeDO;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.mapper.IterativeWorktableMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public List<PriorityDistributeDTO> queryPriorityDistribute(Long projectId, Long sprintId) {
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
        return ConvertHelper.convertList(iterativeWorktableMapper.queryStatusCategoryDistribute(projectId, sprintId), StatusCategoryDTO.class);
    }

    @Override
    public SprintInfoDTO querySprintInfo(Long projectId, Long sprintId) {
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        if (sprintDO == null) {
            throw new CommonException("error.sprint.get");
        }
        SprintInfoDTO result = ConvertHelper.convert(sprintDO, SprintInfoDTO.class);
        List<AssigneeIssueDO> assigneeIssueDOList = iterativeWorktableMapper.queryAssigneeInfoBySprintId(projectId, sprintId);
        result.setAssigneeIssueDTOList(iterativeWorktableAssembler.assigneeIssueDOToDTO(assigneeIssueDOList));
        if (result.getEndDate() != null) {
            result.setDayRemain(DateUtil.differentDaysByMillisecond(new Date(), result.getEndDate()));
        }
        return result;
    }

}
