package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.BoardDependCreateVO;
import io.choerodon.agile.api.vo.BoardDependVO;
import io.choerodon.agile.app.service.BoardDependService;
import io.choerodon.agile.app.service.BoardFeatureService;
import io.choerodon.agile.infra.dataobject.BoardDependDTO;
import io.choerodon.agile.infra.mapper.BoardDependMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
@Service
public class BoardDependServiceImpl implements BoardDependService {

    @Autowired
    private BoardDependMapper boardDependMapper;
//    @Autowired
//    private BoardDependRepository boardDependRepository;
//    @Autowired
//    private BoardFeatureRepository boardFeatureRepository;
    @Autowired
    private BoardFeatureService boardFeatureService;

    public static final String UPDATE_ERROR = "error.boardDepend.update";
    public static final String DELETE_ERROR = "error.boardDepend.deleteById";
    public static final String INSERT_ERROR = "error.boardDepend.create";
    public static final String EXIST_ERROR = "error.boardDepend.existData";
    private static final String ERROR_BOARDDEPEND_ILLEGAL = "error.boardDepend.illegal";
    private static final String ERROR_BOARDDEPEND_CREATE = "error.boardDepend.create";
    private static final String ERROR_BOARDDEPEND_DELETE = "error.boardDepend.delete";
    private static final String ERROR_BOARDDEPEND_NOTFOUND = "error.boardDepend.notFound";
    private static final String ERROR_BOARDDEPEND_UPDATE = "error.boardDepend.update";

    private ModelMapper modelMapper = new ModelMapper();
    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public BoardDependVO create(Long projectId, BoardDependCreateVO createVO) {
        boardFeatureService.checkId(projectId, createVO.getBoardFeatureId());
        boardFeatureService.checkId(projectId, createVO.getDependBoardFeatureId());
        BoardDependDTO boardDependDTO = modelMapper.map(createVO, BoardDependDTO.class);
        boardDependDTO.setProgramId(projectId);
        checkExist(boardDependDTO);
//        boardDependRepository.create(boardDependDTO);
        if (boardDependMapper.insert(boardDependDTO) != 1) {
            throw new CommonException(ERROR_BOARDDEPEND_CREATE);
        }
        return queryById(projectId, boardDependDTO.getId());
    }

    /**
     * 判断是否已经存在
     *
     * @param boardDependDTO
     */
    private void checkExist(BoardDependDTO boardDependDTO) {
        if (!boardDependMapper.select(boardDependDTO).isEmpty()) {
            throw new CommonException(EXIST_ERROR);
        }
    }

    @Override
    public BoardDependVO queryById(Long projectId, Long boardDependId) {
        return modelMapper.map(queryByIdAndCheck(projectId, boardDependId), BoardDependVO.class);
    }

    @Override
    public void deleteById(Long projectId, Long boardDependId) {
//        boardDependRepository.checkId(projectId, boardDependId);
        checkId(projectId, boardDependId);
//        boardDependRepository.delete(boardDependId);
        if (boardDependMapper.deleteByPrimaryKey(boardDependId) != 1) {
            throw new CommonException(ERROR_BOARDDEPEND_DELETE);
        }
    }

    public BoardDependDTO queryByIdAndCheck(Long projectId, Long boardDependId) {
        BoardDependDTO boardDepend = boardDependMapper.selectByPrimaryKey(boardDependId);
        if (boardDepend == null) {
            throw new CommonException(ERROR_BOARDDEPEND_NOTFOUND);
        }
        if (!boardDepend.getProgramId().equals(projectId)) {
            throw new CommonException(ERROR_BOARDDEPEND_ILLEGAL);
        }
        return boardDepend;
    }

    public void checkId(Long projectId, Long boardDependId) {
        queryByIdAndCheck(projectId, boardDependId);
    }
}
