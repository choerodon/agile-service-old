package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.SprintDTO;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintService {

    SprintDetailDTO createSprint(Long projectId);

    SprintDetailDTO updateSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO);

    Boolean deleteSprint(Long projectId, Long sprintId);

    Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds);

    List<SprintNameDTO> queryNameByOptions(Long projectId, List<String> sprintStatusCodes);

    SprintDetailDTO startSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO);

    Boolean completeSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO);

    SprintCompleteMessageDTO queryCompleteMessageBySprintId(Long projectId, Long sprintId);

    SprintDTO getActiveSprint(Long projectId);

    SprintDetailDTO querySprintById(Long projectId, Long sprintId);

    PageInfo<IssueListDTO> queryIssueByOptions(Long projectId, Long sprintId, String status, PageRequest pageRequest, Long organizationId);

    String getQuickFilter(List<Long> quickFilterIds);

    String queryCurrentSprintCreateName(Long projectId);

    SprintDetailDTO createBySprintName(Long projectId, String sprintName);

    List<SprintUnClosedDTO> queryUnClosedSprint(Long projectId);

    ActiveSprintDTO queryActiveSprint(Long projectId, Long organizationId);

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
}
