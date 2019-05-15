package io.choerodon.agile.infra.repository;


import io.choerodon.agile.infra.dataobject.BoardSprintAttrDO;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public interface BoardSprintAttrRepository {
    BoardSprintAttrDO create(Long projectId, Long sprintId, int columnWidth);

    void delete(Long projectId, Long sprintId);

    void update(BoardSprintAttrDO update);

    BoardSprintAttrDO queryBySprintId(Long projectId, Long sprintId);
}
