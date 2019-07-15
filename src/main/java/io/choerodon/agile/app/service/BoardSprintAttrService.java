package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.BoardSprintAttrVO;
import io.choerodon.agile.infra.dataobject.BoardSprintAttrDTO;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public interface BoardSprintAttrService {

    BoardSprintAttrVO updateColumnWidth(Long projectId, Long sprintId, Integer columnWidth);

    BoardSprintAttrDTO queryBySprintId(Long projectId, Long sprintId);

    BoardSprintAttrDTO create(Long projectId, Long sprintId, int columnWidth);

    void update(BoardSprintAttrDTO update);
}
