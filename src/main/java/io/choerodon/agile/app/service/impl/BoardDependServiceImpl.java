package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.BoardDependCreateDTO;
import io.choerodon.agile.api.dto.BoardDependDTO;
import io.choerodon.agile.api.dto.BoardDependUpdateDTO;
import io.choerodon.agile.app.service.BoardDependService;
import io.choerodon.agile.infra.dataobject.BoardDependDO;
import io.choerodon.agile.infra.mapper.BoardDependMapper;
import io.choerodon.agile.infra.repository.BoardDependRepository;
import io.choerodon.agile.infra.repository.BoardFeatureRepository;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
@Service
public class BoardDependServiceImpl implements BoardDependService {

    @Autowired
    private BoardDependMapper boardDependMapper;
    @Autowired
    private BoardDependRepository boardDependRepository;
    @Autowired
    private BoardFeatureRepository boardFeatureRepository;

    public static final String UPDATE_ERROR = "error.boardDepend.update";
    public static final String DELETE_ERROR = "error.boardDepend.deleteById";
    public static final String INSERT_ERROR = "error.boardDepend.create";
    public static final String EXIST_ERROR = "error.boardDepend.existData";
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public BoardDependDTO create(Long projectId, BoardDependCreateDTO createDTO) {
        boardFeatureRepository.checkId(projectId, createDTO.getBoardFeatureId());
        boardFeatureRepository.checkId(projectId, createDTO.getDependBoardFeatureId());
        BoardDependDO boardDependDO = modelMapper.map(createDTO, BoardDependDO.class);
        boardDependDO.setProgramId(projectId);
        checkExist(boardDependDO);
        boardDependRepository.create(boardDependDO);
        return queryById(projectId, boardDependDO.getId());
    }

    @Override
    public BoardDependDTO update(Long projectId, Long boardDependId, BoardDependUpdateDTO updateDTO) {
        boardFeatureRepository.checkId(projectId, updateDTO.getBoardFeatureId());
        boardFeatureRepository.checkId(projectId, updateDTO.getDependBoardFeatureId());
        boardDependRepository.checkId(projectId, boardDependId);
        BoardDependDO update = modelMapper.map(updateDTO, BoardDependDO.class);
        update.setId(boardDependId);
        update.setProgramId(projectId);
        checkExist(update);
        boardDependRepository.update(update);
        return queryById(projectId, boardDependId);
    }

    /**
     * 判断是否已经存在
     *
     * @param boardDependDO
     */
    private void checkExist(BoardDependDO boardDependDO) {
        if (!boardDependMapper.select(boardDependDO).isEmpty()) {
            throw new CommonException(EXIST_ERROR);
        }
    }

    @Override
    public BoardDependDTO queryById(Long projectId, Long boardDependId) {
        return modelMapper.map(boardDependRepository.queryById(projectId, boardDependId), BoardDependDTO.class);
    }

    @Override
    public void deleteById(Long projectId, Long boardDependId) {
        boardDependRepository.checkId(projectId, boardDependId);
        boardDependRepository.delete(boardDependId);
    }
}
