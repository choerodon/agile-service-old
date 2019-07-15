package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.BoardTeamVO;
import io.choerodon.agile.api.vo.BoardTeamUpdateVO;
import io.choerodon.agile.infra.dataobject.BoardTeamDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
public interface BoardTeamService {

    BoardTeamDTO create(Long programId, Long teamProjectId);

    BoardTeamVO update(Long programId, Long boardTeamId, BoardTeamUpdateVO updateVO);

    List<BoardTeamDTO> queryByProgramId(Long programId);
}
