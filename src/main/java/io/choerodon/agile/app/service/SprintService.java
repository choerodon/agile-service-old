package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.SprintConvertDTO;
import io.choerodon.agile.infra.dataobject.SprintDTO;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintService {

    SprintDetailVO createSprint(Long projectId);

    SprintDetailVO updateSprint(Long projectId, SprintUpdateVO sprintUpdateVO);

    Boolean deleteSprint(Long projectId, Long sprintId);

    Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds);

    List<SprintNameVO> queryNameByOptions(Long projectId, List<String> sprintStatusCodes);

    SprintDetailVO startSprint(Long projectId, SprintUpdateVO sprintUpdateVO);

    Boolean completeSprint(Long projectId, SprintCompleteVO sprintCompleteVO);

    SprintCompleteMessageVO queryCompleteMessageBySprintId(Long projectId, Long sprintId);

    SprintDTO getActiveSprint(Long projectId);

    SprintDetailVO querySprintById(Long projectId, Long sprintId);

    PageInfo<IssueListVO> queryIssueByOptions(Long projectId, Long sprintId, String status, PageRequest pageRequest, Long organizationId);

    String getQuickFilter(List<Long> quickFilterIds);

    String queryCurrentSprintCreateName(Long projectId);

    SprintDetailVO createBySprintName(Long projectId, String sprintName);

    List<SprintUnClosedVO> queryUnClosedSprint(Long projectId);

    ActiveSprintVO queryActiveSprint(Long projectId, Long organizationId);

    /**
     * 查询冲刺期间非工作日
     *
     * @param projectId      projectId
     * @param sprintId       sprintId
     * @param organizationId organizationId
     * @return Date
     */
    List<String> queryNonWorkdays(Long projectId, Long sprintId, Long organizationId);

    Boolean checkName(Long projectId, String sprinName);

    void addSprintsWhenJoinProgram(Long programId, Long projectId);

    void completeSprintsByActivePi(Long programId, Long projectId);


    SprintConvertDTO create(SprintConvertDTO sprintConvertDTO);

    SprintConvertDTO update(SprintConvertDTO sprintConvertDTO);

    Boolean delete(SprintConvertDTO sprintConvertDTO);

    void updateSprintNameByBatch(Long programId, List<Long> sprintIds);
}
