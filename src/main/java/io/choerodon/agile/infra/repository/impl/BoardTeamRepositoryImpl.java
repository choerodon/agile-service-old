package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.dataobject.BoardTeamDO;
import io.choerodon.agile.infra.mapper.BoardTeamMapper;
import io.choerodon.agile.infra.repository.BoardTeamRepository;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
@Component
public class BoardTeamRepositoryImpl implements BoardTeamRepository {
    @Autowired
    private BoardTeamMapper boardTeamMapper;

    private static final String ERROR_BOARDTEAM_ILLEGAL = "error.boardTeam.illegal";
    private static final String ERROR_BOARDTEAM_CREATE = "error.boardTeam.create";
    private static final String ERROR_BOARDTEAM_DELETE = "error.boardTeam.delete";
    private static final String ERROR_BOARDTEAM_NOTFOUND = "error.boardTeam.notFound";
    private static final String ERROR_BOARDTEAM_UPDATE = "error.boardTeam.update";

    @Override
    public BoardTeamDO create(BoardTeamDO boardTeam) {
        if (boardTeamMapper.insert(boardTeam) != 1) {
            throw new CommonException(ERROR_BOARDTEAM_CREATE);
        }
        return boardTeamMapper.selectByPrimaryKey(boardTeam.getId());
    }

    @Override
    public void delete(Long boardTeamId) {
        if (boardTeamMapper.deleteByPrimaryKey(boardTeamId) != 1) {
            throw new CommonException(ERROR_BOARDTEAM_DELETE);
        }
    }

    @Override
    public void update(BoardTeamDO boardTeam) {
        if (boardTeamMapper.updateByPrimaryKeySelective(boardTeam) != 1) {
            throw new CommonException(ERROR_BOARDTEAM_UPDATE);
        }
    }

    @Override
    public BoardTeamDO queryById(Long programId, Long boardTeamId) {
        BoardTeamDO boardTeam = boardTeamMapper.selectByPrimaryKey(boardTeamId);
        if (boardTeam == null) {
            throw new CommonException(ERROR_BOARDTEAM_NOTFOUND);
        }
        if (!boardTeam.getProgramId().equals(programId)) {
            throw new CommonException(ERROR_BOARDTEAM_ILLEGAL);
        }
        return boardTeam;
    }

    @Override
    public List<BoardTeamDO> queryByProgramId(Long programId) {
        BoardTeamDO select = new BoardTeamDO();
        select.setProgramId(programId);
        return boardTeamMapper.select(select);
    }
}
