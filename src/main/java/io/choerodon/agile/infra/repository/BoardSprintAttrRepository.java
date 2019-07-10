package io.choerodon.agile.infra.repository;


import io.choerodon.agile.infra.dataobject.BoardSprintAttrDTO;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public interface BoardSprintAttrRepository {
    BoardSprintAttrDTO create(Long projectId, Long sprintId, int columnWidth);

    void delete(Long projectId, Long sprintId);

    void update(BoardSprintAttrDTO update);

    BoardSprintAttrDTO queryBySprintId(Long projectId, Long sprintId);
}
