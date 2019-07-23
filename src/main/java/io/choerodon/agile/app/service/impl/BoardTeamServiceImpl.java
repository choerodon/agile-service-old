package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.BoardTeamVO;
import io.choerodon.agile.api.vo.BoardTeamUpdateVO;
import io.choerodon.agile.app.service.BoardTeamService;
import io.choerodon.agile.infra.utils.RankUtil;
import io.choerodon.agile.infra.dataobject.BoardTeamDTO;
import io.choerodon.agile.infra.mapper.BoardTeamMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
@Service
public class BoardTeamServiceImpl implements BoardTeamService {

    private static final String ERROR_BOARDTEAM_CREATE = "error.boardTeam.create";
    private static final String ERROR_BOARDTEAM_UPDATE = "error.boardTeam.update";
    private static final String ERROR_BOARDTEAM_NOTFOUND = "error.boardTeam.notFound";
    private static final String ERROR_BOARDTEAM_ILLEGAL = "error.boardTeam.illegal";

    @Autowired
    private BoardTeamMapper boardTeamMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public BoardTeamDTO create(Long programId, Long teamProjectId) {
        BoardTeamDTO team = new BoardTeamDTO();
        team.setTeamProjectId(teamProjectId);
        team.setProgramId(programId);
        String minRank = boardTeamMapper.queryMinRank(programId);
        if (minRank == null) {
            team.setRank(RankUtil.mid());
        } else {
            team.setRank(RankUtil.genPre(minRank));
        }
        if (boardTeamMapper.insert(team) != 1) {
            throw new CommonException(ERROR_BOARDTEAM_CREATE);
        }
        return boardTeamMapper.selectByPrimaryKey(team.getId());
    }

    @Override
    public BoardTeamVO update(Long programId, Long boardTeamId, BoardTeamUpdateVO updateVO) {
        BoardTeamDTO boardTeamDTO = modelMapper.map(updateVO, BoardTeamDTO.class);
        boardTeamDTO.setId(boardTeamId);
        boardTeamDTO.setProgramId(programId);
        String outSetRank = queryById(programId, updateVO.getOutsetId()).getRank();
        if (updateVO.getBefore()) {
            boardTeamDTO.setRank(RankUtil.genNext(outSetRank));
        } else {
            String rightRank = boardTeamMapper.queryRightRank(boardTeamDTO, outSetRank);
            if (rightRank == null) {
                boardTeamDTO.setRank(RankUtil.genPre(outSetRank));
            } else {
                boardTeamDTO.setRank(RankUtil.between(outSetRank, rightRank));
            }
        }
        if (boardTeamMapper.updateByPrimaryKeySelective(boardTeamDTO) != 1) {
            throw new CommonException(ERROR_BOARDTEAM_UPDATE);
        }
        return modelMapper.map(queryById(programId, boardTeamId), BoardTeamVO.class);
    }

    public BoardTeamDTO queryById(Long programId, Long boardTeamId) {
        BoardTeamDTO boardTeam = boardTeamMapper.selectByPrimaryKey(boardTeamId);
        if (boardTeam == null) {
            throw new CommonException(ERROR_BOARDTEAM_NOTFOUND);
        }
        if (!boardTeam.getProgramId().equals(programId)) {
            throw new CommonException(ERROR_BOARDTEAM_ILLEGAL);
        }
        return boardTeam;
    }

    @Override
    public List<BoardTeamDTO> queryByProgramId(Long programId) {
        BoardTeamDTO select = new BoardTeamDTO();
        select.setProgramId(programId);
        return boardTeamMapper.select(select);
    }
}
