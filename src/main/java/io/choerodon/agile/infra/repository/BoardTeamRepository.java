package io.choerodon.agile.infra.repository;


import io.choerodon.agile.infra.dataobject.BoardTeamDO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
public interface BoardTeamRepository {
    BoardTeamDO create(BoardTeamDO create);

    void delete(Long boardFeatureId);

    void update(BoardTeamDO update);

    BoardTeamDO queryById(Long programId, Long boardTeamId);

    List<BoardTeamDO> queryByProgramId(Long programId);
}
