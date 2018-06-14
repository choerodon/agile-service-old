package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.BoardDTO;
import io.choerodon.agile.api.dto.IssueMoveDTO;

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

    JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds);

    void initBoard(Long projectId, String boardName);

    IssueMoveDTO move(Long projectId, Long issueId, IssueMoveDTO issueMoveDTO);

    List<BoardDTO> queryByProjectId(Long projectId);
}
