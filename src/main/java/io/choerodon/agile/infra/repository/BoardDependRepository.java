package io.choerodon.agile.infra.repository;


import io.choerodon.agile.infra.dataobject.BoardDependDO;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardDependRepository {
    BoardDependDO create(BoardDependDO create);

    void delete(Long boardDependId);

    void update(BoardDependDO update);

    BoardDependDO queryById(Long projectId, Long boardDependId);

    void checkId(Long projectId, Long boardDependId);
}
