package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.BoardTeamDTO;
import io.choerodon.agile.api.dto.BoardTeamUpdateDTO;
import io.choerodon.agile.app.service.BoardTeamService;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.dataobject.BoardTeamDO;
import io.choerodon.agile.infra.mapper.BoardTeamMapper;
import io.choerodon.agile.infra.repository.BoardTeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
@Service
public class BoardTeamServiceImpl implements BoardTeamService {
    @Autowired
    private BoardTeamRepository boardTeamRepository;
    @Autowired
    private BoardTeamMapper boardTeamMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public BoardTeamDO create(Long programId, Long teamProjectId) {
        BoardTeamDO team = new BoardTeamDO();
        team.setTeamProjectId(teamProjectId);
        team.setProgramId(programId);
        team.setRank(RankUtil.mid());
        return boardTeamRepository.create(team);
    }

    @Override
    public BoardTeamDTO update(Long programId, Long boardTeamId, BoardTeamUpdateDTO updateDTO) {
        BoardTeamDO boardTeamDO = modelMapper.map(updateDTO, BoardTeamDO.class);
        boardTeamDO.setId(boardTeamId);
        String outSetRank = boardTeamRepository.queryById(programId, updateDTO.getOutsetId()).getRank();
        if (updateDTO.getBefore()) {
            boardTeamDO.setRank(RankUtil.genNext(outSetRank));
        } else {
            String rightRank = boardTeamMapper.queryRightRank(boardTeamDO, outSetRank);
            if (rightRank == null) {
                boardTeamDO.setRank(RankUtil.genPre(outSetRank));
            } else {
                boardTeamDO.setRank(RankUtil.between(outSetRank, rightRank));
            }
        }
        boardTeamRepository.update(boardTeamDO);
        return modelMapper.map(boardTeamRepository.queryById(programId, boardTeamId), BoardTeamDTO.class);
    }
}
