package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.IssueStatusVO;
import io.choerodon.agile.api.vo.StatusAndIssuesVO;
import io.choerodon.agile.api.vo.StatusMoveVO;
import io.choerodon.agile.api.vo.event.AddStatusWithProject;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.infra.dataobject.IssueStatusDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueStatusService {

    IssueStatusVO create(Long projectId, String applyType, IssueStatusVO issueStatusVO);

    IssueStatusVO createStatusByStateMachine(Long projectId, IssueStatusVO issueStatusVO);

    IssueStatusVO moveStatusToColumn(Long projectId, Long statusId, StatusMoveVO statusMoveVO);

    List<StatusAndIssuesVO> queryUnCorrespondStatus(Long projectId, Long boardId, String applyType);

    IssueStatusVO moveStatusToUnCorrespond(Long projectId, Long statusId, StatusMoveVO statusMoveVO);

    void deleteStatus(Long projectId, Long statusId, String applyType);

    void consumDeleteStatus(StatusPayload statusPayload);

    /**
     * 查询issueStatus列表
     *
     * @param projectId projectId
     * @return IssueStatusVO
     */
    List<IssueStatusVO> queryIssueStatusList(Long projectId);

    IssueStatusVO updateStatus(Long projectId, IssueStatusVO issueStatusVO);

    IssueStatusDTO insertIssueStatus(IssueStatusDTO issueStatusDTO);

//    IssueStatusDTO update(IssueStatusDTO issueStatusDTO);

    void delete(IssueStatusDTO issueStatusDTO);

    void batchCreateStatusByProjectIds(List<AddStatusWithProject> addStatusWithProjects, Long userId);
}
