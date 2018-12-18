package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.BoardDTO;
import io.choerodon.agile.api.dto.IssueMoveDTO;
import io.choerodon.agile.api.dto.UserSettingDTO;
import io.choerodon.agile.domain.agile.event.StatusPayload;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardService {

    void create(Long projectId, String boardName);

    BoardDTO update(Long projectId, Long boardId, BoardDTO boardDTO);

    void delete(Long projectId, Long boardId);

    BoardDTO queryScrumBoardById(Long projectId, Long boardId);

    JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds,Long organizationId, List<Long> assigneeFilterIds);

    void initBoard(Long projectId, String boardName, List<StatusPayload> statusPayloads);

    IssueMoveDTO move(Long projectId, Long issueId, Long transformId, IssueMoveDTO issueMoveDTO);

    List<BoardDTO> queryByProjectId(Long projectId);

    /**
     * 查询用户看板设置
     *
     * @param projectId projectId
     * @param boardId   boardId
     * @return UserSettingDTO
     */
    UserSettingDTO queryUserSettingBoard(Long projectId, Long boardId);

    /**
     * 更新用户swimLaneBasedCode设置
     *
     * @param projectId         projectId
     * @param boardId           boardId
     * @param swimlaneBasedCode swimlaneBasedCode
     * @return UserSettingDTO
     */
    UserSettingDTO updateUserSettingBoard(Long projectId, Long boardId, String swimlaneBasedCode);

    Boolean checkName(Long projectId, String boardName);
}
