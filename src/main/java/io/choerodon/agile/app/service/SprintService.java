package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
public interface SprintService {
    SprintDetailDTO createSprint(Long projectId);

    SprintDetailDTO updateSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO);

    Boolean deleteSprint(Long projectId, Long sprintId);

    Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap, List<Long> quickFilterIds);

    List<SprintNameDTO> queryNameByOptions(Long projectId, List<String> sprintStatusCodes);

    SprintDetailDTO startSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO);

    Boolean completeSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO);

    SprintCompleteMessageDTO queryCompleteMessageBySprintId(Long projectId, Long sprintId);

    SprintDO getActiveSprint(Long projectId);

    SprintDetailDTO querySprintById(Long projectId, Long sprintId);

    Page<IssueListDTO> queryIssueByOptions(Long projectId, Long sprintId, String status, PageRequest pageRequest);

    String getQuickFilter(List<Long> quickFilterIds);

    String queryCurrentSprintCreateName(Long projectId);

    SprintDetailDTO createBySprintName(Long projectId, String sprintName);

    List<SprintUnClosedDTO> queryUnClosedSprint(Long projectId);

    ActiveSprintDTO queryActiveSprint(Long projectId);

    /**
     * 查询冲刺期间非工作日
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @return Date
     */
    List<String> queryNonWorkdays(Long projectId, Long sprintId);
}
