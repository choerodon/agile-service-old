package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.BoardTeamDTO;
import io.choerodon.agile.api.vo.BoardTeamUpdateDTO;
import io.choerodon.agile.infra.dataobject.BoardTeamDO;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
public interface BoardTeamService {

    BoardTeamDO create(Long programId, Long teamProjectId);

    BoardTeamDTO update(Long programId, Long boardTeamId, BoardTeamUpdateDTO updateDTO);
}
