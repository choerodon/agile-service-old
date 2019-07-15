package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.vo.event.StatusPayload;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface BoardService {

    void create(Long projectId, String boardName);

    BoardVO update(Long projectId, Long boardId, BoardVO boardVO);

    void delete(Long projectId, Long boardId);

    BoardVO queryScrumBoardById(Long projectId, Long boardId);

    JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds,Long organizationId, List<Long> assigneeFilterIds);

    void initBoard(Long projectId, String boardName, List<StatusPayload> statusPayloads);

    IssueMoveVO move(Long projectId, Long issueId, Long transformId, IssueMoveVO issueMoveVO, Boolean isDemo);

    FeatureMoveVO moveByProgram(Long projectId, Long issueId, Long transformId, FeatureMoveVO featureMoveVO);

    List<BoardVO> queryByProjectId(Long projectId);

    /**
     * 查询用户看板设置
     *
     * @param projectId projectId
     * @param boardId   boardId
     * @return UserSettingVO
     */
    UserSettingVO queryUserSettingBoard(Long projectId, Long boardId);

    /**
     * 更新用户swimLaneBasedCode设置
     *
     * @param projectId         projectId
     * @param boardId           boardId
     * @param swimlaneBasedCode swimlaneBasedCode
     * @return UserSettingVO
     */
    UserSettingVO updateUserSettingBoard(Long projectId, Long boardId, String swimlaneBasedCode);

    Boolean checkName(Long projectId, String boardName);

    void initBoardByProgram(Long projectId, String boardName, List<StatusPayload> statusPayloads);

    JSONObject queryByOptionsInProgram(Long projectId, Long boardId, Long organizationId, SearchVO searchVO);
}
