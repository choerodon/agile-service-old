//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.infra.dataobject.BoardDependDTO;
//import io.choerodon.agile.infra.mapper.BoardDependMapper;
//import io.choerodon.agile.infra.repository.BoardDependRepository;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author shinan.chen
// * @since 2019/5/13
// */
//@Component
//public class BoardDependRepositoryImpl implements BoardDependRepository {
//    @Autowired
//    private BoardDependMapper boardDependMapper;
//
//    private static final String ERROR_BOARDDEPEND_ILLEGAL = "error.boardDepend.illegal";
//    private static final String ERROR_BOARDDEPEND_CREATE = "error.boardDepend.create";
//    private static final String ERROR_BOARDDEPEND_DELETE = "error.boardDepend.delete";
//    private static final String ERROR_BOARDDEPEND_NOTFOUND = "error.boardDepend.notFound";
//    private static final String ERROR_BOARDDEPEND_UPDATE = "error.boardDepend.update";
//
//    @Override
//    public BoardDependDTO create(BoardDependDTO boardDepend) {
//        if (boardDependMapper.insert(boardDepend) != 1) {
//            throw new CommonException(ERROR_BOARDDEPEND_CREATE);
//        }
//        return boardDependMapper.selectByPrimaryKey(boardDepend.getId());
//    }
//
//    @Override
//    public void delete(Long boardDependId) {
//        if (boardDependMapper.deleteByPrimaryKey(boardDependId) != 1) {
//            throw new CommonException(ERROR_BOARDDEPEND_DELETE);
//        }
//    }
//
//    @Override
//    public void update(BoardDependDTO boardDepend) {
//        if (boardDependMapper.updateByPrimaryKeySelective(boardDepend) != 1) {
//            throw new CommonException(ERROR_BOARDDEPEND_UPDATE);
//        }
//    }
//
//    @Override
//    public BoardDependDTO queryById(Long projectId, Long boardDependId) {
//        BoardDependDTO boardDepend = boardDependMapper.selectByPrimaryKey(boardDependId);
//        if (boardDepend == null) {
//            throw new CommonException(ERROR_BOARDDEPEND_NOTFOUND);
//        }
//        if (!boardDepend.getProgramId().equals(projectId)) {
//            throw new CommonException(ERROR_BOARDDEPEND_ILLEGAL);
//        }
//        return boardDepend;
//    }
//
//    @Override
//    public void checkId(Long projectId, Long boardDependId) {
//        queryById(projectId, boardDependId);
//    }
//}
